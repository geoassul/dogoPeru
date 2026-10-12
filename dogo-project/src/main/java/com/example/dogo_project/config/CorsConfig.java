package com.example.dogo_project.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración CORS única, aplicada por Spring Security.
 *
 * Los orígenes salen de app.cors.allowed-origins (lista separada por comas, admite comodines
 * como http://192.168.1.*:8080 para pruebas desde el celular en la red local).
 * Antes se permitía cualquier origen con credenciales, lo que dejaba a cualquier web llamar a la API.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(parseOrigins(allowedOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "Content-Length", "X-Total-Count"));
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        // Catálogo público de solo lectura: cualquier origen puede leerlo (la web publicada, Live Server o
        // la web abierta como archivo local, cuyo origen es "null"). Sin credenciales: no da acceso a nada privado.
        CorsConfiguration publica = new CorsConfiguration();
        publica.setAllowedOriginPatterns(List.of("*"));
        publica.setAllowedMethods(List.of("GET", "OPTIONS"));
        publica.setAllowedHeaders(List.of("Accept", "Content-Type"));
        publica.setAllowCredentials(false);
        publica.setMaxAge(maxAge);

        // El primer patrón que coincide gana: /api/public/** debe registrarse antes que /**
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/public/**", publica);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static List<String> parseOrigins(String origins) {
        return Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
    }
}
