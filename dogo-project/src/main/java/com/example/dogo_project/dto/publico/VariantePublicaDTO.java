package com.example.dogo_project.dto.publico;

/**
 * Variante visible en la web pública. No incluye costo ni stock exacto: solo si está disponible
 * y si quedan pocas unidades.
 */
public record VariantePublicaDTO(
        Integer id,
        String sku,
        String talla,
        String color,
        String tipoManga,
        Double precio,
        boolean disponible,
        boolean ultimasUnidades) {
}
