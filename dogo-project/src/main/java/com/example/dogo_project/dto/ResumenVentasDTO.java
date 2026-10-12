package com.example.dogo_project.dto;

/**
 * Cantidad de ventas y monto total de un periodo. Se construye directamente en la consulta JPQL.
 */
public record ResumenVentasDTO(Long cantidad, Double monto) {

    public ResumenVentasDTO {
        cantidad = cantidad != null ? cantidad : 0L;
        monto = monto != null ? Math.round(monto * 100) / 100.0 : 0.0;
    }
}
