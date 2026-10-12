package com.example.dogo_project.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dogo_project.dto.ResumenVentasDTO;
import com.example.dogo_project.model.Venta;

/**
 * Cada consulta trae la venta, sus detalles y la variante de cada detalle en un único SELECT.
 * VentaDTO solo necesita el SKU de la variante; producto, categoría y proveedor no se cargan.
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    String CON_DETALLES = "SELECT DISTINCT v FROM Venta v LEFT JOIN FETCH v.detalles d LEFT JOIN FETCH d.variante ";

    @Query(CON_DETALLES + "ORDER BY v.id")
    List<Venta> findAllConDetalles();

    @Query(CON_DETALLES + "WHERE v.id = :id")
    Optional<Venta> findByIdConDetalles(@Param("id") Integer id);

    @Query(CON_DETALLES + "WHERE v.fechaVenta BETWEEN :inicio AND :fin ORDER BY v.fechaVenta")
    List<Venta> findByFechaVentaBetweenConDetalles(@Param("inicio") LocalDateTime inicio,
                                                   @Param("fin") LocalDateTime fin);

    @Query(CON_DETALLES + "WHERE v.id IN :ids ORDER BY v.fechaVenta DESC, v.id DESC")
    List<Venta> findByIdInConDetalles(@Param("ids") Collection<Integer> ids);

    /**
     * Ids de las ventas más recientes. Se pagina sobre los ids y no sobre el JOIN FETCH de la
     * colección, para que el LIMIT lo haga MySQL y no Hibernate en memoria.
     */
    @Query("SELECT v.id FROM Venta v ORDER BY v.fechaVenta DESC, v.id DESC")
    List<Integer> findRecentIds(Pageable pageable);

    // ==================== DASHBOARD (agregados en una sola consulta cada uno) ====================

    @Query("SELECT new com.example.dogo_project.dto.ResumenVentasDTO(COUNT(v), SUM(v.totalVenta)) "
         + "FROM Venta v WHERE v.fechaVenta >= :desde AND v.fechaVenta < :hasta")
    ResumenVentasDTO resumenEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT new com.example.dogo_project.dto.ResumenVentasDTO(COUNT(v), SUM(v.totalVenta)) FROM Venta v")
    ResumenVentasDTO resumenHistorico();
}
