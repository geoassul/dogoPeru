package com.example.dogo_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.dogo_project.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     */
    boolean existsByUsername(String username);
    
    /**
     * Cuenta usuarios de un rol (para no borrar ni degradar al último ADMIN)
     */
    long countByRole(String role);

    List<User> findAllByOrderByIdAsc();
}
