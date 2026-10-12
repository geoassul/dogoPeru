package com.example.dogo_project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.dogo_project.model.User;
import com.example.dogo_project.repository.UserRepository;

/**
 * Servicio personalizado para cargar detalles del usuario desde la base de datos
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario en la base de datos
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado con username: " + username));

        // Retorna una instancia de SecurityUser que implementa UserDetails
        return new SecurityUser(user);
    }
}
