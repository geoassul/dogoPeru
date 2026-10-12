package com.example.dogo_project.service;

import com.example.dogo_project.model.VarianteProducto;
import java.util.List;
import java.util.Optional;

public interface VarianteProductoService {
    List<VarianteProducto> findAll();
    Optional<VarianteProducto> findById(Integer id);
    VarianteProducto save(VarianteProducto varianteProducto);
    void deleteById(Integer id);
    List<VarianteProducto> findByProducto(Integer productoId);
    List<VarianteProducto> findByStockGreaterThan(Integer stock);
    VarianteProducto findBySku(String sku);
}
