package com.example.dogo_project.dto;

import java.util.List;

/**
 * Respuesta de GET /api/dashboard.
 */
public record DashboardDTO(
        ResumenVentasDTO hoy,
        ResumenVentasDTO mes,
        ResumenVentasDTO historico,
        Double gananciaMes,
        Double gananciaHistorica,
        int umbralStockBajo,
        List<VarianteProductoDTO> stockBajo,
        List<TopVarianteDTO> masVendidas,
        List<VentaDTO> ventasRecientes) {
}
