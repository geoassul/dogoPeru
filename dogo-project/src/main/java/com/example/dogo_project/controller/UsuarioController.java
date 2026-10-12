package com.example.dogo_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dogo_project.dto.UserDTO;
import com.example.dogo_project.dto.UsuarioRequest;
import com.example.dogo_project.model.User;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.security.SecurityUser;
import com.example.dogo_project.service.UsuarioService;

import jakarta.validation.Valid;

/**
 * Gestión de usuarios (solo ADMIN, ver SecurityConfig). Nunca devuelve la contraseña.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(usuarioService.findAll().stream().map(UsuarioController::toDto).toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getById(@PathVariable Long id) {
        return usuarioService.findById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success(toDto(user))))
                .orElseGet(UsuarioController::notFound);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> crear(@Valid @RequestBody UsuarioRequest request) {
        User user = usuarioService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Usuario creado exitosamente", toDto(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody UsuarioRequest request,
                                                           @AuthenticationPrincipal SecurityUser actual) {
        return usuarioService.actualizar(id, request, actual.getId())
                .map(user -> ResponseEntity.ok(ApiResponse.success("Usuario actualizado exitosamente", toDto(user))))
                .orElseGet(UsuarioController::notFound);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> eliminar(@PathVariable Long id,
                                                        @AuthenticationPrincipal SecurityUser actual) {
        if (!usuarioService.eliminar(id, actual.getId())) {
            return notFound();
        }
        return ResponseEntity.ok(ApiResponse.success("Usuario eliminado exitosamente", null));
    }

    private static UserDTO toDto(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getRole());
    }

    private static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Usuario no encontrado"));
    }
}
