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
import com.example.dogo_project.dto.ProductoDTO;
import com.example.dogo_project.exception.BusinessException;
import com.example.dogo_project.model.Categoria;
import com.example.dogo_project.model.Producto;
import com.example.dogo_project.model.Proveedor;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.CategoriaService;
import com.example.dogo_project.service.ProductoService;
import com.example.dogo_project.service.ProveedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> getAllProductos() {
        return ResponseEntity.ok(ApiResponse.success(toDtos(productoService.findAll())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoDTO>> getProductoById(@PathVariable Integer id) {
        return productoService.findById(id)
                .map(producto -> ResponseEntity.ok(ApiResponse.success(DtoMapper.toDto(producto))))
                .orElseGet(ProductoController::notFound);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoDTO>> createProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        Producto producto = new Producto();
        aplicarCambios(producto, productoDTO);
        Producto saved = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Producto creado exitosamente", DtoMapper.toDto(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoDTO>> updateProducto(@PathVariable Integer id,
                                                                   @Valid @RequestBody ProductoDTO productoDTO) {
        Optional<Producto> existente = productoService.findById(id);
        if (existente.isEmpty()) {
            return notFound();
        }
        Producto producto = existente.get();
        aplicarCambios(producto, productoDTO);
        Producto updated = productoService.save(producto);
        return ResponseEntity.ok(ApiResponse.success("Producto actualizado exitosamente", DtoMapper.toDto(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProducto(@PathVariable Integer id) {
        if (productoService.findById(id).isEmpty()) {
            return notFound();
        }
        productoService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Producto eliminado exitosamente", null));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> getProductosByCategoria(@PathVariable Integer categoriaId) {
        return ResponseEntity.ok(ApiResponse.success(toDtos(productoService.findByCategoria(categoriaId))));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<ApiResponse<List<ProductoDTO>>> getProductosByProveedor(@PathVariable Integer proveedorId) {
        return ResponseEntity.ok(ApiResponse.success(toDtos(productoService.findByProveedor(proveedorId))));
    }

    private void aplicarCambios(Producto producto, ProductoDTO dto) {
        Categoria categoria = categoriaService.findById(dto.getCategoriaId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Categoría no encontrada"));
        Proveedor proveedor = proveedorService.findById(dto.getProveedorId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Proveedor no encontrado"));
        producto.setNombre(dto.getNombre());
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
    }

    private static List<ProductoDTO> toDtos(List<Producto> productos) {
        return productos.stream().map(DtoMapper::toDto).toList();
    }

    private static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Producto no encontrado"));
    }
}
