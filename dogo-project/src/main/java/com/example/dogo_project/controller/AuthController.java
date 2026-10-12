package com.example.dogo_project.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dogo_project.dto.JwtResponse;
import com.example.dogo_project.dto.LoginRequest;
import com.example.dogo_project.dto.UserDTO;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.security.JwtUtil;
import com.example.dogo_project.security.SecurityUser;

import lombok.RequiredArgsConstructor;

/**
 * Controlador para manejo de autenticación
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    /**
     * Endpoint para login de usuarios
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), 
                    loginRequest.getPassword()
                )
            );

            // Obtener usuario autenticado
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            UserDTO userDTO = modelMapper.map(securityUser.getUser(), UserDTO.class);

            // Generar JWT token real
            String token = jwtUtil.generateToken(securityUser);

            JwtResponse jwtResponse = new JwtResponse(token, userDTO);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Login exitoso", jwtResponse));

        } catch (AuthenticationException e) {
            // Mensaje genérico: no revela si el usuario existe ni detalles internos
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Usuario o contraseña incorrectos", null));
        }
    }

    /**
     * Endpoint para validar token (opcional)
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<String>> validateToken() {
        // Si llegamos aquí, el token es válido (por el filtro de seguridad)
        return ResponseEntity.ok(new ApiResponse<>(true, "Token válido", "OK"));
    }

    /**
     * Endpoint para verificar token específicamente para sesiones
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<UserDTO>> verifyToken() {
        try {
            // Si llegamos aquí, el token es válido (verificado por el filtro JWT)
            // Retornamos información del usuario actual
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
                SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
                UserDTO userDTO = modelMapper.map(securityUser.getUser(), UserDTO.class);
                return ResponseEntity.ok(new ApiResponse<>(true, "Token verificado correctamente", userDTO));
            }
            
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Usuario no autenticado", null));
                
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, "Token inválido: " + e.getMessage(), null));
        }
    }
}
