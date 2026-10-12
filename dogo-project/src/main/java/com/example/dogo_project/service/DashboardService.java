package com.example.dogo_project.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dogo_project.dto.DashboardDTO;
import com.example.dogo_project.dto.DtoMapper;
import com.example.dogo_project.repository.DetalleVentaRepository;
import com.example.dogo_project.repository.VarianteProductoRepository;
import com.example.dogo_project.repository.VentaRepository;

/**
 * Resumen para el panel de administración. Cada dato sale de una consulta agregada,
 * así que el número de consultas es fijo sin importar cuántas ventas haya.
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final int TOP_VENDIDAS = 5;
    private static final int VENTAS_RECIENTES = 5;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private VarianteProductoRepository varianteProductoRepository;

    @Autowired
    private VentaService ventaService;

    public DashboardDTO resumen(int umbralStockBajo) {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime manana = inicioHoy.plusDays(1);

        return new DashboardDTO(
                ventaRepository.resumenEntre(inicioHoy, manana),
                ventaRepository.resumenEntre(inicioMes, manana),
                ventaRepository.resumenHistorico(),
                redondear(detalleVentaRepository.gananciaEntre(inicioMes, manana)),
                redondear(detalleVentaRepository.gananciaHistorica()),
                umbralStockBajo,
                varianteProductoRepository.findStockBajo(umbralStockBajo).stream().map(DtoMapper::toDto).toList(),
                detalleVentaRepository.masVendidas(PageRequest.of(0, TOP_VENDIDAS)),
                ventaService.findRecent(VENTAS_RECIENTES).stream().map(DtoMapper::toDto).toList());
    }

    private static Double redondear(Double valor) {
        return valor != null ? Math.round(valor * 100) / 100.0 : 0.0;
    }
}
