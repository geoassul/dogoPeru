package com.example.dogo_project.service;

import java.util.List;
import java.util.Optional;

import com.example.dogo_project.dto.UsuarioRequest;
import com.example.dogo_project.model.User;

public interface UsuarioService {

    List<User> findAll();

    Optional<User> findById(Long id);

    User crear(UsuarioRequest request);

    /** Cambia usuario y rol, y la contraseña solo si viene informada. */
    Optional<User> actualizar(Long id, UsuarioRequest request, Long idUsuarioActual);

    boolean eliminar(Long id, Long idUsuarioActual);
}
