package com.example.dogo_project.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.dogo_project.model.User;

/**
 * Implementación de UserDetails para integrar la entidad User con Spring Security
 */
public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convierte el rol del usuario en una autoridad de Spring Security
        // Agrega el prefijo "ROLE_" que es la convención de Spring Security
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Por ahora todas las cuentas no expiran
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Por ahora todas las cuentas no están bloqueadas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Por ahora las credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        return true; // Por ahora todos los usuarios están habilitados
    }

    /**
     * Método para obtener la entidad User original
     */
    public User getUser() {
        return user;
    }

    /**
     * Método para obtener el ID del usuario
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Método para obtener el rol sin el prefijo ROLE_
     */
    public String getRole() {
        return user.getRole();
    }
}
