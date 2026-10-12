package com.example.dogo_project.service;

import com.example.dogo_project.model.ImagenVariante;
import java.util.List;
import java.util.Optional;

public interface ImagenVarianteService {
    List<ImagenVariante> findAll();
    Optional<ImagenVariante> findById(Integer id);
    ImagenVariante save(ImagenVariante imagenVariante);
    void deleteById(Integer id);
    List<ImagenVariante> findByVariante(Integer varianteId);
}
