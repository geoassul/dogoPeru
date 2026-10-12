package com.example.dogo_project.dto.publico;

/**
 * Categoría con al menos un producto, con la imagen principal de uno de ellos (puede ser null).
 */
public record CategoriaPublicaDTO(Integer id, String nombre, long productos, String imagen) {
}
