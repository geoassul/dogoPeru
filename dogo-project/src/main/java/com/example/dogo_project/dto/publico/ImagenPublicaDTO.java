package com.example.dogo_project.dto.publico;

/**
 * Imagen de un producto. `url` es la ruta guardada en la BD (p. ej. /img/polos/...),
 * que la web resuelve contra su propio hosting.
 */
public record ImagenPublicaDTO(String url, String tipo, String alt, Integer varianteId) {
}
