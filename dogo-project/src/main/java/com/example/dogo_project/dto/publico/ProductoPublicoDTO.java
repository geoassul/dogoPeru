package com.example.dogo_project.dto.publico;

import java.util.List;

/**
 * Producto del catálogo público, con sus variantes e imágenes ya agrupadas.
 * `vendidos` sirve para ordenar destacados; `precioDesde` es el menor precio de sus variantes.
 */
public record ProductoPublicoDTO(
        Integer id,
        String nombre,
        Integer categoriaId,
        String categoria,
        Double precioDesde,
        boolean disponible,
        List<String> tallas,
        List<String> colores,
        long vendidos,
        List<VariantePublicaDTO> variantes,
        List<ImagenPublicaDTO> imagenes) {
}
