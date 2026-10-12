package com.example.dogo_project.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapper se usa solo para DTOs planos (UserDTO, CategoriaDTO -> Categoria, ProveedorDTO -> Proveedor).
 * Las respuestas con relaciones se construyen con DtoMapper para no disparar cargas LAZY (N+1).
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
