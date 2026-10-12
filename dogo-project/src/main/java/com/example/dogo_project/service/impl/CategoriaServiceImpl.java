package com.example.dogo_project.service.impl;

import com.example.dogo_project.model.Categoria;
import com.example.dogo_project.repository.CategoriaRepository;
import com.example.dogo_project.service.CategoriaService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    @Cacheable(value = "categorias", key = "'all'")
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Override
    @Cacheable(value = "categorias", key = "#id")
    public Optional<Categoria> findById(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Override
    @CacheEvict(value = "categorias", allEntries = true)
    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Override
    @CacheEvict(value = "categorias", allEntries = true)
    public void deleteById(Integer id) {
        categoriaRepository.deleteById(id);
    }
}
