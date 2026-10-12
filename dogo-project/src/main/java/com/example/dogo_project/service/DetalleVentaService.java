package com.example.dogo_project.service;

import com.example.dogo_project.model.DetalleVenta;
import com.example.dogo_project.model.DetalleVentaId;
import java.util.List;
import java.util.Optional;

public interface DetalleVentaService {
    List<DetalleVenta> findAll();
    Optional<DetalleVenta> findById(DetalleVentaId id);
    DetalleVenta save(DetalleVenta detalleVenta);
    void deleteById(DetalleVentaId id);
    List<DetalleVenta> findByVenta(Integer ventaId);
    List<DetalleVenta> findByVariante(Integer varianteId);
}
