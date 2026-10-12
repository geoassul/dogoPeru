package com.example.dogo_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagenVarianteDTO {
    private Integer id;
    private Integer varianteId;
    private String urlImagen;
    private String nombreArchivo;
    private String tipoImagen;
    private Byte orden;
    private String altText;
}
