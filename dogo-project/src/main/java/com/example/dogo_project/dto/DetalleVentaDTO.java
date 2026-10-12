package com.example.dogo_project.dto;

public class DetalleVentaDTO {
    private Integer ventaId;
    private Integer varianteId;
    private String varianteSku;
    private Integer cantidad;
    private Double precioUnitarioVenta;
    private Double costoUnitarioAdquisicion;

    public DetalleVentaDTO() {}

    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Integer getVarianteId() { return varianteId; }
    public void setVarianteId(Integer varianteId) { this.varianteId = varianteId; }
    public String getVarianteSku() { return varianteSku; }
    public void setVarianteSku(String varianteSku) { this.varianteSku = varianteSku; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitarioVenta() { return precioUnitarioVenta; }
    public void setPrecioUnitarioVenta(Double precioUnitarioVenta) { this.precioUnitarioVenta = precioUnitarioVenta; }
    public Double getCostoUnitarioAdquisicion() { return costoUnitarioAdquisicion; }
    public void setCostoUnitarioAdquisicion(Double costoUnitarioAdquisicion) { this.costoUnitarioAdquisicion = costoUnitarioAdquisicion; }
}
