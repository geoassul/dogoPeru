package com.example.dogo_project.controller;

import java.util.List;
import java.util.Optional;

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

import com.example.dogo_project.dto.DtoMapper;
import com.example.dogo_project.dto.VarianteProductoDTO;
import com.example.dogo_project.exception.BusinessException;
import com.example.dogo_project.model.Producto;
import com.example.dogo_project.model.VarianteProducto;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.ProductoService;
import com.example.dogo_project.service.VarianteProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/variantes-producto")
public class VarianteProductoController {

    @Autowired
    private VarianteProductoService varianteProductoService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VarianteProductoDTO>>> getAllVariantes() {
        return ResponseEntity.ok(ApiResponse.success(toDtos(varianteProductoService.findAll())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VarianteProductoDTO>> getVarianteById(@PathVariable Integer id) {
        return varianteProductoService.findById(id)
                .map(variante -> ResponseEntity.ok(ApiResponse.success(DtoMapper.toDto(variante))))
                .orElseGet(VarianteProductoController::notFound);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VarianteProductoDTO>> createVariante(@Valid @RequestBody VarianteProductoDTO varianteDTO) {
        VarianteProducto variante = new VarianteProducto();
        aplicarCambios(variante, varianteDTO);
        VarianteProducto saved = varianteProductoService.save(variante);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Variante creada exitosamente", DtoMapper.toDto(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VarianteProductoDTO>> updateVariante(@PathVariable Integer id,
                                                                           @Valid @RequestBody VarianteProductoDTO varianteDTO) {
        Optional<VarianteProducto> existente = varianteProductoService.findById(id);
        if (existente.isEmpty()) {
            return notFound();
        }
        VarianteProducto variante = existente.get();
        aplicarCambios(variante, varianteDTO);
        VarianteProducto updated = varianteProductoService.save(variante);
        return ResponseEntity.ok(ApiResponse.success("Variante actualizada exitosamente", DtoMapper.toDto(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteVariante(@PathVariable Integer id) {
        if (varianteProductoService.findById(id).isEmpty()) {
            return notFound();
        }
        varianteProductoService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Variante eliminada exitosamente", null));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponse<List<VarianteProductoDTO>>> getVariantesByProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(ApiResponse.success(toDtos(varianteProductoService.findByProducto(productoId))));
    }

    @GetMapping("/stock/{minStock}")
    public ResponseEntity<ApiResponse<List<VarianteProductoDTO>>> getVariantesByStock(@PathVariable Integer minStock) {
        return ResponseEntity.ok(ApiResponse.success(toDtos(varianteProductoService.findByStockGreaterThan(minStock))));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<VarianteProductoDTO>> getVarianteBySku(@PathVariable String sku) {
        VarianteProducto variante = varianteProductoService.findBySku(sku);
        if (variante == null) {
            return notFound();
        }
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toDto(variante)));
    }

    private void aplicarCambios(VarianteProducto variante, VarianteProductoDTO dto) {
        if (dto.getProductoId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "productoId es obligatorio");
        }
        if (dto.getStock() != null && dto.getStock() < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "El stock no puede ser negativo");
        }
        Producto producto = productoService.findById(dto.getProductoId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Producto no encontrado"));
        variante.setProducto(producto);
        variante.setSku(dto.getSku());
        variante.setTalla(dto.getTalla());
        variante.setTipoManga(dto.getTipoManga());
        variante.setColor(dto.getColor());
        variante.setCostoAdquisicion(dto.getCostoAdquisicion());
        variante.setPrecioVenta(dto.getPrecioVenta());
        variante.setStock(dto.getStock() != null ? dto.getStock() : 0);
    }

    private static List<VarianteProductoDTO> toDtos(List<VarianteProducto> variantes) {
        return variantes.stream().map(DtoMapper::toDto).toList();
    }

    private static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Variante no encontrada"));
    }
}
