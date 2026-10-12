package com.example.dogo_project.service;

import com.example.dogo_project.model.Proveedor;
import java.util.List;
import java.util.Optional;

public interface ProveedorService {
    List<Proveedor> findAll();
    Optional<Proveedor> findById(Integer id);
    Proveedor save(Proveedor proveedor);
    void deleteById(Integer id);
}
