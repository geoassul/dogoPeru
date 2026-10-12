package com.example.dogo_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dogo_project.dto.CategoriaDTO;
import com.example.dogo_project.dto.DtoMapper;
import com.example.dogo_project.model.Categoria;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaDTO>>> getAllCategorias() {
        List<CategoriaDTO> categorias = categoriaService.findAll().stream().map(DtoMapper::toDto).toList();
        return ResponseEntity.ok(ApiResponse.success(categorias));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaDTO>> getCategoriaById(@PathVariable Integer id) {
        return categoriaService.findById(id)
                .map(categoria -> ResponseEntity.ok(ApiResponse.success(DtoMapper.toDto(categoria))))
                .orElseGet(CategoriaController::notFound);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaDTO>> createCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre());
        Categoria saved = categoriaService.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Categoría creada exitosamente", DtoMapper.toDto(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaDTO>> updateCategoria(@PathVariable Integer id,
                                                                     @Valid @RequestBody CategoriaDTO categoriaDTO) {
        if (categoriaService.findById(id).isEmpty()) {
            return notFound();
        }
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(categoriaDTO.getNombre());
        Categoria updated = categoriaService.save(categoria);
        return ResponseEntity.ok(ApiResponse.success("Categoría actualizada exitosamente", DtoMapper.toDto(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategoria(@PathVariable Integer id) {
        if (categoriaService.findById(id).isEmpty()) {
            return notFound();
        }
        categoriaService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Categoría eliminada exitosamente", null));
    }

    private static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Categoría no encontrada"));
    }
}
