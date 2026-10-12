package com.example.dogo_project.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

    @EmbeddedId
    private DetalleVentaId id = new DetalleVentaId();

    // @MapsId: la clave compuesta toma los ids de venta y variante al persistir,
    // lo que permite guardar los detalles en cascada junto con una venta nueva
    @MapsId("ventaId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @MapsId("varianteId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id")
    private VarianteProducto variante;

    private Integer cantidad;

    @Column(name = "precio_unitario_venta")
    private Double precioUnitarioVenta;

    @Column(name = "costo_unitario_adquisicion")
    private Double costoUnitarioAdquisicion;

    // Constructors
    public DetalleVenta() {}
    
    public DetalleVenta(DetalleVentaId id) {
        this.id = id;
    }

    // Getters y Setters
    public DetalleVentaId getId() { return id; }
    public void setId(DetalleVentaId id) { this.id = id; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { 
        this.venta = venta; 
        if (venta != null) {
            if (this.id == null) {
                this.id = new DetalleVentaId();
            }
            this.id.setVentaId(venta.getId());
        }
    }
    public VarianteProducto getVariante() { return variante; }
    public void setVariante(VarianteProducto variante) { 
        this.variante = variante;
        if (variante != null) {
            if (this.id == null) {
                this.id = new DetalleVentaId();
            }
            this.id.setVarianteId(variante.getId());
        }
    }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Double getPrecioUnitarioVenta() { return precioUnitarioVenta; }
    public void setPrecioUnitarioVenta(Double precioUnitarioVenta) { this.precioUnitarioVenta = precioUnitarioVenta; }
    public Double getCostoUnitarioAdquisicion() { return costoUnitarioAdquisicion; }
    public void setCostoUnitarioAdquisicion(Double costoUnitarioAdquisicion) { this.costoUnitarioAdquisicion = costoUnitarioAdquisicion; }
}
