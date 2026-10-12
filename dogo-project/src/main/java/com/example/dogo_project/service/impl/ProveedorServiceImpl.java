package com.example.dogo_project.service.impl;

import com.example.dogo_project.model.Proveedor;
import com.example.dogo_project.repository.ProveedorRepository;
import com.example.dogo_project.service.ProveedorService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    @Cacheable(value = "proveedores", key = "'all'")
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    @Override
    @Cacheable(value = "proveedores", key = "#id")
    public Optional<Proveedor> findById(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Override
    @CacheEvict(value = "proveedores", allEntries = true)
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    @CacheEvict(value = "proveedores", allEntries = true)
    public void deleteById(Integer id) {
        proveedorRepository.deleteById(id);
    }
}
