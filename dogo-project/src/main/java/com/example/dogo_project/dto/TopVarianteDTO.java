package com.example.dogo_project.dto;

/**
 * Variante más vendida (unidades acumuladas). Se construye directamente en la consulta JPQL.
 */
public record TopVarianteDTO(Integer varianteId, String sku, String productoNombre, Long unidades) {
}
