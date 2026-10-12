package com.example.dogo_project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dogo_project.model.ImagenVariante;
import com.example.dogo_project.repository.ImagenVarianteRepository;
import com.example.dogo_project.service.ImagenVarianteService;

@Service
@Transactional(readOnly = true)
public class ImagenVarianteServiceImpl implements ImagenVarianteService {

    @Autowired
    private ImagenVarianteRepository imagenVarianteRepository;

    @Override
    public List<ImagenVariante> findAll() {
        return imagenVarianteRepository.findAllOrdenadas();
    }

    @Override
    public Optional<ImagenVariante> findById(Integer id) {
        return imagenVarianteRepository.findById(id);
    }

    @Override
    @Transactional
    public ImagenVariante save(ImagenVariante imagenVariante) {
        return imagenVarianteRepository.save(imagenVariante);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        imagenVarianteRepository.deleteById(id);
    }

    @Override
    public List<ImagenVariante> findByVariante(Integer varianteId) {
        return imagenVarianteRepository.findByVarianteId(varianteId);
    }
}
