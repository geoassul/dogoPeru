package com.example.dogo_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dogo_project.model.Producto;

/**
 * Las lecturas traen categoría y proveedor en el mismo SELECT (JOIN FETCH)
 * para evitar el problema N+1 al mapear a ProductoDTO.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor ORDER BY p.id")
    List<Producto> findAllConRelaciones();

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.id = :id")
    Optional<Producto> findByIdConRelaciones(@Param("id") Integer id);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.categoria.id = :categoriaId ORDER BY p.id")
    List<Producto> findByCategoriaId(@Param("categoriaId") Integer categoriaId);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categoria LEFT JOIN FETCH p.proveedor WHERE p.proveedor.id = :proveedorId ORDER BY p.id")
    List<Producto> findByProveedorId(@Param("proveedorId") Integer proveedorId);
}
