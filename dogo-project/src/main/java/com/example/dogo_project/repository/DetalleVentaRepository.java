package com.example.dogo_project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dogo_project.dto.TopVarianteDTO;
import com.example.dogo_project.model.DetalleVenta;
import com.example.dogo_project.model.DetalleVentaId;

/**
 * DetalleVentaDTO necesita el SKU de la variante, así que la variante se trae con JOIN FETCH.
 * Producto, categoría y proveedor no se cargan porque la respuesta no los usa.
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, DetalleVentaId> {

    @Query("SELECT d FROM DetalleVenta d JOIN FETCH d.variante ORDER BY d.id.ventaId, d.id.varianteId")
    List<DetalleVenta> findAllConVariante();

    @Query("SELECT d FROM DetalleVenta d JOIN FETCH d.variante WHERE d.id = :id")
    Optional<DetalleVenta> findByIdConVariante(@Param("id") DetalleVentaId id);

    @Query("SELECT d FROM DetalleVenta d JOIN FETCH d.variante WHERE d.id.ventaId = :ventaId ORDER BY d.id.varianteId")
    List<DetalleVenta> findByVentaId(@Param("ventaId") Integer ventaId);

    @Query("SELECT d FROM DetalleVenta d JOIN FETCH d.variante WHERE d.id.varianteId = :varianteId ORDER BY d.id.ventaId")
    List<DetalleVenta> findByVarianteId(@Param("varianteId") Integer varianteId);

    // ==================== DASHBOARD ====================

    /**
     * Ganancia = (precio de venta - costo) x cantidad. Si la línea no guardó el costo se usa
     * el costo actual de la variante.
     */
    String GANANCIA = "SUM((COALESCE(d.precioUnitarioVenta, 0.0) "
            + "- COALESCE(d.costoUnitarioAdquisicion, var.costoAdquisicion, 0.0)) * d.cantidad) "
            + "FROM DetalleVenta d JOIN d.variante var ";

    @Query("SELECT " + GANANCIA + "WHERE d.venta.fechaVenta >= :desde AND d.venta.fechaVenta < :hasta")
    Double gananciaEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT " + GANANCIA)
    Double gananciaHistorica();

    @Query("SELECT new com.example.dogo_project.dto.TopVarianteDTO(var.id, var.sku, p.nombre, SUM(d.cantidad)) "
         + "FROM DetalleVenta d JOIN d.variante var JOIN var.producto p "
         + "GROUP BY var.id, var.sku, p.nombre ORDER BY SUM(d.cantidad) DESC")
    List<TopVarianteDTO> masVendidas(Pageable pageable);

    /** Unidades vendidas por producto: cada fila es [productoId (Integer), unidades (Long)]. */
    @Query("SELECT p.id, SUM(d.cantidad) FROM DetalleVenta d JOIN d.variante var JOIN var.producto p GROUP BY p.id")
    List<Object[]> unidadesVendidasPorProducto();
}
