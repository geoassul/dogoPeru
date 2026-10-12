package com.example.dogo_project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.dogo_project.dto.VentaCompletaDTO;
import com.example.dogo_project.model.Venta;

/**
 * Todas las lecturas devuelven la venta con sus detalles y la variante de cada detalle
 * ya cargados (una sola consulta), listos para convertir a VentaDTO.
 */
public interface VentaService {

    List<Venta> findAll();
    Optional<Venta> findById(Integer id);
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Venta> findRecent(int limite);

    /** Solo actualiza la fecha: el total y el stock dependen de los detalles. */
    Optional<Venta> actualizarFecha(Integer id, LocalDateTime fechaVenta);

    boolean deleteById(Integer id);

    /** Crea una venta sin detalles (flujo antiguo: las líneas se agregan con /api/detalle-venta). */
    Venta crearCabecera(LocalDateTime fechaVenta, Double totalVenta);

    /**
     * Registra la venta y sus detalles en una transacción: valida y descuenta stock
     * (con bloqueo de filas), toma precio y costo de la variante y calcula el total.
     */
    Venta registrarVentaCompleta(VentaCompletaDTO venta);
}
