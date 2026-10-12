package com.example.dogo_project.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DetalleVentaId implements Serializable {
    
    @Column(name = "venta_id")
    private Integer ventaId;
    
    @Column(name = "variante_id")
    private Integer varianteId;

    public DetalleVentaId() {}

    public DetalleVentaId(Integer ventaId, Integer varianteId) {
        this.ventaId = ventaId;
        this.varianteId = varianteId;
    }

    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Integer getVarianteId() { return varianteId; }
    public void setVarianteId(Integer varianteId) { this.varianteId = varianteId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVentaId that = (DetalleVentaId) o;
        return Objects.equals(ventaId, that.ventaId) && Objects.equals(varianteId, that.varianteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ventaId, varianteId);
    }
}
