package com.example.dogo_project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dogo_project.model.DetalleVenta;
import com.example.dogo_project.model.DetalleVentaId;
import com.example.dogo_project.repository.DetalleVentaRepository;
import com.example.dogo_project.service.DetalleVentaService;

@Service
@Transactional(readOnly = true)
public class DetalleVentaServiceImpl implements DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Override
    public List<DetalleVenta> findAll() {
        return detalleVentaRepository.findAllConVariante();
    }

    @Override
    public Optional<DetalleVenta> findById(DetalleVentaId id) {
        return detalleVentaRepository.findByIdConVariante(id);
    }

    @Override
    @Transactional
    public DetalleVenta save(DetalleVenta detalleVenta) {
        // La clave compuesta nunca es null, así que save() siempre hace merge y devuelve la variante
        // como proxy sin cargar: se relee con JOIN FETCH dentro de la transacción
        DetalleVenta saved = detalleVentaRepository.save(detalleVenta);
        return detalleVentaRepository.findByIdConVariante(saved.getId()).orElseThrow();
    }

    @Override
    @Transactional
    public void deleteById(DetalleVentaId id) {
        detalleVentaRepository.deleteById(id);
    }

    @Override
    public List<DetalleVenta> findByVenta(Integer ventaId) {
        return detalleVentaRepository.findByVentaId(ventaId);
    }

    @Override
    public List<DetalleVenta> findByVariante(Integer varianteId) {
        return detalleVentaRepository.findByVarianteId(varianteId);
    }
}
