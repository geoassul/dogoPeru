package com.example.dogo_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dogo_project.model.ImagenVariante;

/**
 * ImagenVarianteDTO solo usa el id de la variante, que Hibernate toma de la FK
 * sin consultar la tabla de variantes.
 */
@Repository
public interface ImagenVarianteRepository extends JpaRepository<ImagenVariante, Integer> {

    @Query("SELECT i FROM ImagenVariante i ORDER BY i.variante.id, i.orden")
    List<ImagenVariante> findAllOrdenadas();

    @Query("SELECT i FROM ImagenVariante i WHERE i.variante.id = :varianteId ORDER BY i.orden")
    List<ImagenVariante> findByVarianteId(@Param("varianteId") Integer varianteId);
}
