package com.example.dogo_project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.dogo_project.model.User;
import com.example.dogo_project.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Crea usuarios de prueba (admin/admin y user/user) solo con el perfil "dev".
 * Sin ese perfil no hace nada, para no dejar contraseñas triviales en otros entornos.
 */
@Component
@Profile("dev")
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando carga de datos de prueba...");
        
        // Crear usuario ADMIN si no existe
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            log.info("Usuario ADMIN creado: admin/admin");
        } else {
            log.info("Usuario ADMIN ya existe");
        }

        // Crear usuario USER si no existe
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole("USER");
            userRepository.save(user);
            log.info("Usuario USER creado: user/user");
        } else {
            log.info("Usuario USER ya existe");
        }

        // Verificar usuarios creados
        long userCount = userRepository.count();
        log.info("Total de usuarios en base de datos: {}", userCount);
        
        // Mostrar usuarios existentes
        userRepository.findAll().forEach(u -> 
            log.info("Usuario: {} - Rol: {}", u.getUsername(), u.getRole())
        );
    }
}
