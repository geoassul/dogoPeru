package com.example.dogo_project.service.impl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dogo_project.dto.VentaCompletaDTO;
import com.example.dogo_project.exception.BusinessException;
import com.example.dogo_project.model.DetalleVenta;
import com.example.dogo_project.model.VarianteProducto;
import com.example.dogo_project.model.Venta;
import com.example.dogo_project.repository.VarianteProductoRepository;
import com.example.dogo_project.repository.VentaRepository;
import com.example.dogo_project.service.VentaService;

@Service
@Transactional(readOnly = true)
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private VarianteProductoRepository varianteProductoRepository;

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAllConDetalles();
    }

    @Override
    public Optional<Venta> findById(Integer id) {
        return ventaRepository.findByIdConDetalles(id);
    }

    @Override
    public List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaVentaBetweenConDetalles(fechaInicio, fechaFin);
    }

    @Override
    public List<Venta> findRecent(int limite) {
        // Dos consultas fijas: ids paginados en MySQL y luego esas ventas con sus detalles
        List<Integer> ids = ventaRepository.findRecentIds(PageRequest.of(0, limite));
        if (ids.isEmpty()) {
            return List.of();
        }
        return ventaRepository.findByIdInConDetalles(ids);
    }

    @Override
    @Transactional
    public Optional<Venta> actualizarFecha(Integer id, LocalDateTime fechaVenta) {
        Optional<Venta> venta = ventaRepository.findByIdConDetalles(id);
        venta.ifPresent(v -> v.setFechaVenta(fechaVenta));
        return venta;
    }

    @Override
    @Transactional
    public boolean deleteById(Integer id) {
        if (!ventaRepository.existsById(id)) {
            return false;
        }
        // Los detalles se eliminan por ON DELETE CASCADE en detalle_venta
        ventaRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public Venta crearCabecera(LocalDateTime fechaVenta, Double totalVenta) {
        Venta venta = new Venta();
        venta.setFechaVenta(fechaVenta != null ? fechaVenta : LocalDateTime.now());
        venta.setTotalVenta(totalVenta);
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public Venta registrarVentaCompleta(VentaCompletaDTO dto) {
        // Agrupar cantidades por variante: la PK de detalle_venta es (venta_id, variante_id)
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (VentaCompletaDTO.DetalleVentaDTO item : dto.getDetalles()) {
            cantidades.merge(item.getVarianteProductoId(), item.getCantidad(), Integer::sum);
        }

        // Bloquea las variantes (SELECT ... FOR UPDATE) hasta el commit: dos ventas simultáneas
        // no pueden leer el mismo stock y dejarlo negativo
        Map<Integer, VarianteProducto> variantes = varianteProductoRepository
                .findAllByIdForUpdate(cantidades.keySet()).stream()
                .collect(Collectors.toMap(VarianteProducto::getId, Function.identity()));

        Venta venta = new Venta();
        venta.setFechaVenta(dto.getFechaVenta() != null ? dto.getFechaVenta() : LocalDateTime.now());
        double total = 0;

        for (Map.Entry<Integer, Integer> entry : cantidades.entrySet()) {
            VarianteProducto variante = variantes.get(entry.getKey());
            int cantidad = entry.getValue();
            if (variante == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "La variante " + entry.getKey() + " no existe");
            }
            if (variante.getPrecioVenta() == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "La variante " + variante.getSku() + " no tiene precio de venta");
            }
            int stock = variante.getStock() != null ? variante.getStock() : 0;
            if (stock < cantidad) {
                throw new BusinessException(HttpStatus.CONFLICT,
                        "Stock insuficiente para " + variante.getSku()
                                + ": disponible " + stock + ", solicitado " + cantidad);
            }
            variante.setStock(stock - cantidad);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVariante(variante);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitarioVenta(variante.getPrecioVenta());
            detalle.setCostoUnitarioAdquisicion(variante.getCostoAdquisicion());
            venta.addDetalle(detalle);

            total += variante.getPrecioVenta() * cantidad;
        }

        venta.setTotalVenta(Math.round(total * 100) / 100.0);
        // persist en cascada: inserta la venta y luego sus detalles; el stock se actualiza al hacer flush
        return ventaRepository.save(venta);
    }
}
