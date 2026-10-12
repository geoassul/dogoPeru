package com.example.dogo_project.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dogo_project.model.VarianteProducto;

import jakarta.persistence.LockModeType;

/**
 * Las lecturas traen el producto en el mismo SELECT (JOIN FETCH),
 * porque VarianteProductoDTO necesita productoId y productoNombre.
 */
@Repository
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Integer> {

    @Query("SELECT v FROM VarianteProducto v JOIN FETCH v.producto ORDER BY v.id")
    List<VarianteProducto> findAllConProducto();

    @Query("SELECT v FROM VarianteProducto v JOIN FETCH v.producto WHERE v.id = :id")
    Optional<VarianteProducto> findByIdConProducto(@Param("id") Integer id);

    @Query("SELECT v FROM VarianteProducto v JOIN FETCH v.producto WHERE v.producto.id = :productoId ORDER BY v.id")
    List<VarianteProducto> findByProductoId(@Param("productoId") Integer productoId);

    @Query("SELECT v FROM VarianteProducto v JOIN FETCH v.producto WHERE v.stock > :stock ORDER BY v.id")
    List<VarianteProducto> findByStockGreaterThan(@Param("stock") Integer stock);

    @Query("SELECT v FROM VarianteProducto v JOIN FETCH v.producto WHERE v.stock <= :umbral ORDER BY v.stock, v.id")
    List<VarianteProducto> findStockBajo(@Param("umbral") Integer umbral);

    @Query("SELECT v FROM VarianteProducto v JOIN FETCH v.producto WHERE v.sku = :sku")
    Optional<VarianteProducto> findBySku(@Param("sku") String sku);

    /**
     * SELECT ... FOR UPDATE: bloquea las variantes mientras se descuenta stock en una venta.
     * Se ordena por id para que ventas concurrentes bloqueen en el mismo orden y no haya deadlocks.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VarianteProducto v WHERE v.id IN :ids ORDER BY v.id")
    List<VarianteProducto> findAllByIdForUpdate(@Param("ids") Collection<Integer> ids);
}
