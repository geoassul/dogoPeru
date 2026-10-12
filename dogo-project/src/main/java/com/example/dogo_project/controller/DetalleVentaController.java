package com.example.dogo_project.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dogo_project.dto.DetalleVentaDTO;
import com.example.dogo_project.dto.DtoMapper;
import com.example.dogo_project.exception.BusinessException;
import com.example.dogo_project.model.DetalleVenta;
import com.example.dogo_project.model.DetalleVentaId;
import com.example.dogo_project.model.VarianteProducto;
import com.example.dogo_project.model.Venta;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.DetalleVentaService;
import com.example.dogo_project.service.VarianteProductoService;
import com.example.dogo_project.service.VentaService;

/**
 * CRUD de líneas de venta sueltas. Para registrar ventas con control de stock usar
 * POST /api/ventas/completa: estos endpoints no modifican stock ni el total de la venta.
 */
@RestController
@RequestMapping("/api/detalle-venta")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VarianteProductoService varianteProductoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetalleVentaDTO>>> getAllDetalles() {
        return ResponseEntity.ok(ApiResponse.success(toDtos(detalleVentaService.findAll())));
    }

    @GetMapping("/{ventaId}/{varianteId}")
    public ResponseEntity<ApiResponse<DetalleVentaDTO>> getDetalleById(@PathVariable Integer ventaId,
                                                                       @PathVariable Integer varianteId) {
        return detalleVentaService.findById(new DetalleVentaId(ventaId, varianteId))
                .map(detalle -> ResponseEntity.ok(ApiResponse.success(DtoMapper.toDto(detalle))))
                .orElseGet(DetalleVentaController::notFound);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DetalleVentaDTO>> createDetalle(@RequestBody DetalleVentaDTO detalleDTO) {
        if (detalleDTO.getVentaId() == null || detalleDTO.getVarianteId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "ventaId y varianteId son obligatorios");
        }
        if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que cero");
        }
        DetalleVentaId id = new DetalleVentaId(detalleDTO.getVentaId(), detalleDTO.getVarianteId());
        if (detalleVentaService.findById(id).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "La venta ya tiene un detalle para esa variante");
        }
        Venta venta = ventaService.findById(detalleDTO.getVentaId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Venta no encontrada"));
        VarianteProducto variante = varianteProductoService.findById(detalleDTO.getVarianteId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Variante no encontrada"));

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setVariante(variante);
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setPrecioUnitarioVenta(detalleDTO.getPrecioUnitarioVenta());
        detalle.setCostoUnitarioAdquisicion(detalleDTO.getCostoUnitarioAdquisicion());

        DetalleVenta saved = detalleVentaService.save(detalle);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Detalle creado exitosamente", DtoMapper.toDto(saved)));
    }

    /** Venta y variante forman la clave primaria: solo se actualizan cantidad, precio y costo. */
    @PutMapping("/{ventaId}/{varianteId}")
    public ResponseEntity<ApiResponse<DetalleVentaDTO>> updateDetalle(@PathVariable Integer ventaId,
                                                                      @PathVariable Integer varianteId,
                                                                      @RequestBody DetalleVentaDTO detalleDTO) {
        Optional<DetalleVenta> existente = detalleVentaService.findById(new DetalleVentaId(ventaId, varianteId));
        if (existente.isEmpty()) {
            return notFound();
        }
        if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que cero");
        }
        DetalleVenta detalle = existente.get();
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setPrecioUnitarioVenta(detalleDTO.getPrecioUnitarioVenta());
        detalle.setCostoUnitarioAdquisicion(detalleDTO.getCostoUnitarioAdquisicion());
        DetalleVenta updated = detalleVentaService.save(detalle);
        return ResponseEntity.ok(ApiResponse.success("Detalle actualizado exitosamente", DtoMapper.toDto(updated)));
    }

    @DeleteMapping("/{ventaId}/{varianteId}")
    public ResponseEntity<ApiResponse<String>> deleteDetalle(@PathVariable Integer ventaId,
                                                             @PathVariable Integer varianteId) {
        DetalleVentaId id = new DetalleVentaId(ventaId, varianteId);
        if (detalleVentaService.findById(id).isEmpty()) {
            return notFound();
        }
        detalleVentaService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Detalle eliminado exitosamente", null));
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<ApiResponse<List<DetalleVentaDTO>>> getDetallesByVenta(@PathVariable Integer ventaId) {
        return ResponseEntity.ok(ApiResponse.success(toDtos(detalleVentaService.findByVenta(ventaId))));
    }

    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<ApiResponse<List<DetalleVentaDTO>>> getDetallesByVariante(@PathVariable Integer varianteId) {
        return ResponseEntity.ok(ApiResponse.success(toDtos(detalleVentaService.findByVariante(varianteId))));
    }

    private static List<DetalleVentaDTO> toDtos(List<DetalleVenta> detalles) {
        return detalles.stream().map(DtoMapper::toDto).toList();
    }

    private static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Detalle no encontrado"));
    }
}
