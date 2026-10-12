package com.example.dogo_project.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Petición para registrar una venta completa (POST /api/ventas/completa).
 *
 * El servidor toma precio y costo de cada variante y calcula el total, así que "total" y
 * "precio_unitario" se aceptan por compatibilidad pero se ignoran: el cliente no puede fijar precios.
 */
public class VentaCompletaDTO {
    
    @JsonProperty("fecha_venta")
    @JsonAlias("fechaVenta")
    private LocalDateTime fechaVenta;
    
    @JsonProperty("total")
    private Double total;
    
    @JsonProperty("metodo_pago")
    @JsonAlias("metodoPago")
    private String metodoPago;
    
    @JsonProperty("detalles")
    @NotEmpty(message = "La venta debe tener al menos un detalle")
    private List<@Valid @NotNull DetalleVentaDTO> detalles;
    
    // Constructors
    public VentaCompletaDTO() {}
    
    public VentaCompletaDTO(LocalDateTime fechaVenta, Double total, String metodoPago, List<DetalleVentaDTO> detalles) {
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.metodoPago = metodoPago;
        this.detalles = detalles;
    }
    
    // Getters and Setters
    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public Double getTotal() {
        return total;
    }
    
    public void setTotal(Double total) {
        this.total = total;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }
    
    /**
     * DTO interno para los detalles de venta
     */
    public static class DetalleVentaDTO {
        
        @JsonProperty("variante_producto_id")
        @JsonAlias({"varianteProductoId", "varianteId", "variante_id"})
        @NotNull(message = "variante_producto_id es obligatorio")
        @Positive(message = "variante_producto_id debe ser positivo")
        private Integer varianteProductoId;
        
        @JsonProperty("cantidad")
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        private Integer cantidad;
        
        @JsonProperty("precio_unitario")
        @JsonAlias("precioUnitario")
        private Double precioUnitario;
        
        // Constructors
        public DetalleVentaDTO() {}
        
        public DetalleVentaDTO(Integer varianteProductoId, Integer cantidad, Double precioUnitario) {
            this.varianteProductoId = varianteProductoId;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }
        
        // Getters and Setters
        public Integer getVarianteProductoId() {
            return varianteProductoId;
        }
        
        public void setVarianteProductoId(Integer varianteProductoId) {
            this.varianteProductoId = varianteProductoId;
        }
        
        public Integer getCantidad() {
            return cantidad;
        }
        
        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
        
        public Double getPrecioUnitario() {
            return precioUnitario;
        }
        
        public void setPrecioUnitario(Double precioUnitario) {
            this.precioUnitario = precioUnitario;
        }
    }
}
