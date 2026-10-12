package com.example.dogo_project.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VentaDTO {
    private Integer id;
    private LocalDateTime fechaVenta;
    private Double totalVenta;
    private List<DetalleVentaDTO> detalles;

    public VentaDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    public Double getTotalVenta() { return totalVenta; }
    public void setTotalVenta(Double totalVenta) { this.totalVenta = totalVenta; }
    public List<DetalleVentaDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVentaDTO> detalles) { this.detalles = detalles; }
}
