package com.example.dogo_project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dogo_project.model.Producto;
import com.example.dogo_project.repository.ProductoRepository;
import com.example.dogo_project.service.ProductoService;

@Service
@Transactional(readOnly = true)
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAllConRelaciones();
    }

    @Override
    public Optional<Producto> findById(Integer id) {
        return productoRepository.findByIdConRelaciones(id);
    }

    @Override
    @Transactional
    public Producto save(Producto producto) {
        // Al actualizar, save() hace merge y devuelve relaciones sin cargar (proxy); se relee con
        // JOIN FETCH dentro de la transacción para poder armar el DTO sin LazyInitializationException
        Producto saved = productoRepository.save(producto);
        return productoRepository.findByIdConRelaciones(saved.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> findByCategoria(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public List<Producto> findByProveedor(Integer proveedorId) {
        return productoRepository.findByProveedorId(proveedorId);
    }
}
