package com.example.dogo_project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dogo_project.dto.UsuarioRequest;
import com.example.dogo_project.exception.BusinessException;
import com.example.dogo_project.model.User;
import com.example.dogo_project.repository.UserRepository;
import com.example.dogo_project.service.UsuarioService;

@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private static final String ADMIN = "ADMIN";
    private static final int MIN_PASSWORD = 6;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userRepository.findAllByOrderByIdAsc();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User crear(UsuarioRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
        validarPassword(request.getPassword());
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un usuario con ese nombre");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> actualizar(Long id, UsuarioRequest request, Long idUsuarioActual) {
        Optional<User> existente = userRepository.findById(id);
        if (existente.isEmpty()) {
            return existente;
        }
        User user = existente.get();

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un usuario con ese nombre");
        }
        boolean pierdeAdmin = ADMIN.equals(user.getRole()) && !ADMIN.equals(request.getRole());
        if (pierdeAdmin && id.equals(idUsuarioActual)) {
            throw new BusinessException(HttpStatus.CONFLICT, "No puedes quitarte el rol ADMIN a ti mismo");
        }
        if (pierdeAdmin && userRepository.countByRole(ADMIN) <= 1) {
            throw new BusinessException(HttpStatus.CONFLICT, "Debe quedar al menos un usuario ADMIN");
        }

        user.setUsername(request.getUsername());
        user.setRole(request.getRole());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            validarPassword(request.getPassword());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return Optional.of(user);
    }

    @Override
    @Transactional
    public boolean eliminar(Long id, Long idUsuarioActual) {
        Optional<User> existente = userRepository.findById(id);
        if (existente.isEmpty()) {
            return false;
        }
        if (id.equals(idUsuarioActual)) {
            throw new BusinessException(HttpStatus.CONFLICT, "No puedes eliminar tu propio usuario");
        }
        if (ADMIN.equals(existente.get().getRole()) && userRepository.countByRole(ADMIN) <= 1) {
            throw new BusinessException(HttpStatus.CONFLICT, "Debe quedar al menos un usuario ADMIN");
        }
        userRepository.deleteById(id);
        return true;
    }

    private static void validarPassword(String password) {
        if (password.length() < MIN_PASSWORD) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres");
        }
    }
}
