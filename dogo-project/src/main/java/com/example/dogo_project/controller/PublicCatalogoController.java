package com.example.dogo_project.controller;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dogo_project.dto.publico.CategoriaPublicaDTO;
import com.example.dogo_project.dto.publico.ProductoPublicoDTO;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.CatalogoPublicoService;

/**
 * Catálogo de solo lectura para la web pública (sin login, ver SecurityConfig).
 * El navegador puede cachear la respuesta un minuto.
 */
@RestController
@RequestMapping("/api/public")
public class PublicCatalogoController {

    private static final CacheControl CACHE = CacheControl.maxAge(Duration.ofSeconds(60)).cachePublic();

    @Autowired
    private CatalogoPublicoService catalogoPublicoService;

    @GetMapping("/catalogo")
    public ResponseEntity<ApiResponse<List<ProductoPublicoDTO>>> catalogo() {
        return ResponseEntity.ok().cacheControl(CACHE).body(ApiResponse.success(catalogoPublicoService.catalogo()));
    }

    @GetMapping("/categorias")
    public ResponseEntity<ApiResponse<List<CategoriaPublicaDTO>>> categorias() {
        return ResponseEntity.ok().cacheControl(CACHE).body(ApiResponse.success(catalogoPublicoService.categorias()));
    }
}
