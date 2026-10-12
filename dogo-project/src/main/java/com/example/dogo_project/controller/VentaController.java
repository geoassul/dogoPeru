package com.example.dogo_project.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dogo_project.dto.DtoMapper;
import com.example.dogo_project.dto.VentaCompletaDTO;
import com.example.dogo_project.dto.VentaDTO;
import com.example.dogo_project.model.Venta;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.VentaService;

import jakarta.validation.Valid;

/**
 * Todas las lecturas usan una única consulta con JOIN FETCH (venta + detalles + variante),
 * así que el número de consultas no crece con la cantidad de ventas (sin N+1).
 *
 * Las rutas /optimized*, /optimized-jpa*, /recent-optimized y /all-data-optimized se conservan
 * por compatibilidad con el frontend y devuelven lo mismo que las rutas principales.
 * Los errores no controlados los responde GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private static final int VENTAS_RECIENTES = 5;

    @Autowired
    private VentaService ventaService;

    @GetMapping({"", "/optimized", "/optimized-jpa", "/all-data-optimized"})
    public ResponseEntity<ApiResponse<List<VentaDTO>>> getAllVentas() {
        return ResponseEntity.ok(ApiResponse.success(toDtos(ventaService.findAll())));
    }

    @GetMapping({"/{id}", "/optimized/{id}", "/optimized-jpa/{id}"})
    public ResponseEntity<ApiResponse<VentaDTO>> getVentaById(@PathVariable Integer id) {
        return ventaService.findById(id)
                .map(venta -> ResponseEntity.ok(ApiResponse.success(DtoMapper.toDto(venta))))
                .orElseGet(() -> notFound());
    }

    @GetMapping("/fecha")
    public ResponseEntity<ApiResponse<List<VentaDTO>>> getVentasByFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(ApiResponse.success(
                toDtos(ventaService.findByFechaVentaBetween(fechaInicio, fechaFin))));
    }

    /** Devuelve la lista sin envolver en ApiResponse, como hacía antes (lo usa el dashboard). */
    @GetMapping("/recent")
    public ResponseEntity<List<VentaDTO>> getRecentSales() {
        return ResponseEntity.ok(toDtos(ventaService.findRecent(VENTAS_RECIENTES)));
    }

    @GetMapping("/recent-optimized")
    public ResponseEntity<ApiResponse<List<VentaDTO>>> getRecentSalesOptimized() {
        return ResponseEntity.ok(ApiResponse.success(toDtos(ventaService.findRecent(VENTAS_RECIENTES))));
    }

    /**
     * Crea una venta. Si trae detalles, se registra igual que /completa (stock y precios desde la BD).
     * Sin detalles se crea solo la cabecera, para el flujo antiguo que luego agrega
     * líneas con /api/detalle-venta.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<VentaDTO>> createVenta(@RequestBody VentaDTO ventaDTO) {
        Venta venta;
        if (ventaDTO.getDetalles() != null && !ventaDTO.getDetalles().isEmpty()) {
            VentaCompletaDTO completa = new VentaCompletaDTO();
            completa.setFechaVenta(ventaDTO.getFechaVenta());
            completa.setDetalles(ventaDTO.getDetalles().stream()
                    .map(d -> new VentaCompletaDTO.DetalleVentaDTO(d.getVarianteId(), d.getCantidad(), null))
                    .toList());
            venta = ventaService.registrarVentaCompleta(completa);
        } else {
            venta = ventaService.crearCabecera(ventaDTO.getFechaVenta(), ventaDTO.getTotalVenta());
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Venta creada exitosamente", DtoMapper.toDto(venta)));
    }

    @PostMapping("/completa")
    public ResponseEntity<ApiResponse<VentaDTO>> registrarVentaCompleta(
            @Valid @RequestBody VentaCompletaDTO ventaCompletaDTO) {
        Venta venta = ventaService.registrarVentaCompleta(ventaCompletaDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Venta registrada exitosamente", DtoMapper.toDto(venta)));
    }

    /** Solo se puede cambiar la fecha; total y stock dependen de los detalles. */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaDTO>> updateVenta(@PathVariable Integer id,
                                                             @RequestBody VentaDTO ventaDTO) {
        if (ventaDTO.getFechaVenta() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("fechaVenta es obligatoria"));
        }
        return ventaService.actualizarFecha(id, ventaDTO.getFechaVenta())
                .map(venta -> ResponseEntity.ok(
                        ApiResponse.success("Venta actualizada exitosamente", DtoMapper.toDto(venta))))
                .orElseGet(() -> notFound());
    }

    @DeleteMapping({"/{id}", "/optimized/{id}"})
    public ResponseEntity<ApiResponse<String>> deleteVenta(@PathVariable Integer id) {
        if (!ventaService.deleteById(id)) {
            return notFound();
        }
        return ResponseEntity.ok(ApiResponse.success("Venta eliminada exitosamente", null));
    }

    private static List<VentaDTO> toDtos(List<Venta> ventas) {
        return ventas.stream().map(DtoMapper::toDto).toList();
    }

    private static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Venta no encontrada"));
    }
}
