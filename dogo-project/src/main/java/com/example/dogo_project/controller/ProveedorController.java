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

import com.example.dogo_project.dto.DtoMapper;
import com.example.dogo_project.dto.ProveedorDTO;
import com.example.dogo_project.model.Proveedor;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.ProveedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProveedorDTO>>> getAllProveedores() {
        List<ProveedorDTO> proveedores = proveedorService.findAll().stream().map(DtoMapper::toDto).toList();
        return ResponseEntity.ok(ApiResponse.success(proveedores));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorDTO>> getProveedorById(@PathVariable Integer id) {
        return proveedorService.findById(id)
                .map(proveedor -> ResponseEntity.ok(ApiResponse.success(DtoMapper.toDto(proveedor))))
                .orElseGet(ProveedorController::notFound);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProveedorDTO>> createProveedor(@Valid @RequestBody ProveedorDTO proveedorDTO) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(proveedorDTO.getNombre());
        proveedor.setTelefono(proveedorDTO.getTelefono());
        Proveedor saved = proveedorService.save(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Proveedor creado exitosamente", DtoMapper.toDto(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorDTO>> updateProveedor(@PathVariable Integer id,
                                                                     @Valid @RequestBody ProveedorDTO proveedorDTO) {
        if (proveedorService.findById(id).isEmpty()) {
            return notFound();
        }
        Proveedor proveedor = new Proveedor();
        proveedor.setId(id);
        proveedor.setNombre(proveedorDTO.getNombre());
        proveedor.setTelefono(proveedorDTO.getTelefono());
        Proveedor updated = proveedorService.save(proveedor);
        return ResponseEntity.ok(ApiResponse.success("Proveedor actualizado exitosamente", DtoMapper.toDto(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProveedor(@PathVariable Integer id) {
        if (proveedorService.findById(id).isEmpty()) {
            return notFound();
        }
        proveedorService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Proveedor eliminado exitosamente", null));
    }

    private static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Proveedor no encontrado"));
    }
}
