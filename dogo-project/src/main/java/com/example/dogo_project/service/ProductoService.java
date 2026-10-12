package com.example.dogo_project.service;

import com.example.dogo_project.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> findAll();
    Optional<Producto> findById(Integer id);
    Producto save(Producto producto);
    void deleteById(Integer id);
    List<Producto> findByCategoria(Integer categoriaId);
    List<Producto> findByProveedor(Integer proveedorId);
}
