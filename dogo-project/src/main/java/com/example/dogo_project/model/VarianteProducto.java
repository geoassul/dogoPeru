package com.example.dogo_project.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "variantes_producto")
public class VarianteProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(length = 100)
    private String sku;

    @Column(length = 20)
    private String talla;

    @Column(name = "tipo_manga", length = 20)
    private String tipoManga;

    @Column(length = 20)
    private String color;

    @Column(name = "costo_adquisicion")
    private Double costoAdquisicion;

    @Column(name = "precio_venta")
    private Double precioVenta;

    private Integer stock;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
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
