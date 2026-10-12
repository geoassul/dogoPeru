package com.example.dogo_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class VarianteProductoDTO {
    private Integer id;
    @NotNull(message = "El producto es obligatorio")
    private Integer productoId;
    private String productoNombre;
    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 100, message = "El SKU no puede superar 100 caracteres")
    private String sku;
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String talla;
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String tipoManga;
    @Size(max = 20, message = "Máximo 20 caracteres")
    private String color;
    @PositiveOrZero(message = "El costo no puede ser negativo")
    private Double costoAdquisicion;
    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double precioVenta;
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    public VarianteProductoDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    public String getTipoManga() { return tipoManga; }
    public void setTipoManga(String tipoManga) { this.tipoManga = tipoManga; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Double getCostoAdquisicion() { return costoAdquisicion; }
    public void setCostoAdquisicion(Double costoAdquisicion) { this.costoAdquisicion = costoAdquisicion; }
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
