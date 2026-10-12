package com.example.dogo_project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dogo_project.model.VarianteProducto;
import com.example.dogo_project.repository.VarianteProductoRepository;
import com.example.dogo_project.service.VarianteProductoService;

@Service
@Transactional(readOnly = true)
public class VarianteProductoServiceImpl implements VarianteProductoService {

    @Autowired
    private VarianteProductoRepository varianteProductoRepository;

    @Override
    public List<VarianteProducto> findAll() {
        return varianteProductoRepository.findAllConProducto();
    }

    @Override
    public Optional<VarianteProducto> findById(Integer id) {
        return varianteProductoRepository.findByIdConProducto(id);
    }

    @Override
    @Transactional
    public VarianteProducto save(VarianteProducto varianteProducto) {
        // save() puede hacer merge y devolver el producto como proxy sin cargar: se relee con JOIN FETCH
        VarianteProducto saved = varianteProductoRepository.save(varianteProducto);
        return varianteProductoRepository.findByIdConProducto(saved.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        varianteProductoRepository.deleteById(id);
    }

    @Override
    public List<VarianteProducto> findByProducto(Integer productoId) {
        return varianteProductoRepository.findByProductoId(productoId);
    }

    @Override
    public List<VarianteProducto> findByStockGreaterThan(Integer stock) {
        return varianteProductoRepository.findByStockGreaterThan(stock);
    }

    @Override
    public VarianteProducto findBySku(String sku) {
        return varianteProductoRepository.findBySku(sku).orElse(null);
    }
}
