package com.example.dogo_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Datos para crear o actualizar un usuario. La contraseña es obligatoria al crear y opcional
 * al actualizar (vacía = no se cambia); esa regla la aplica UsuarioService.
 */
@Data
@NoArgsConstructor
public class UsuarioRequest {

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 3, max = 25, message = "El usuario debe tener entre 3 y 25 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "El usuario solo puede tener letras, números, punto, guion y guion bajo")
    private String username;

    @Size(max = 72, message = "La contraseña no puede superar 72 caracteres")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(ADMIN|USER)$", message = "El rol debe ser ADMIN o USER")
    private String role;
}
