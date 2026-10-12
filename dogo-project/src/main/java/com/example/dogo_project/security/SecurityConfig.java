package com.example.dogo_project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuración de seguridad de la aplicación
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // API REST stateless con JWT en header: no usa cookies de sesión, CSRF no aplica
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            .authorizeHttpRequests(authz -> authz
                // Público
                .requestMatchers("/", "/login.html", "/error").permitAll()
                // Panel de administración: páginas estáticas; los datos los protege la API
                .requestMatchers("/admin", "/admin/", "/admin/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**").permitAll()
                .requestMatchers("/favicon.ico", "/robots.txt", "/manifest.json").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Imágenes de productos subidas al servidor (se muestran en el catálogo)
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // Catálogo de solo lectura para la web pública (sin costo ni stock exacto)
                .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()

                // Solo ADMIN
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/proveedores/**").hasRole("ADMIN")
                .requestMatchers("/api/categorias/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").hasRole("ADMIN")

                // ADMIN y USER
                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")

                // USER (vendedor): consulta el catálogo y las ventas, y registra ventas
                .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/variantes-producto/**",
                        "/api/imagenes-variante/**", "/api/ventas/**", "/api/detalle-venta/**")
                    .hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/api/ventas/completa").hasAnyRole("ADMIN", "USER")

                // Altas, cambios y bajas del catálogo y de ventas: solo ADMIN
                .requestMatchers("/api/productos/**", "/api/variantes-producto/**", "/api/imagenes-variante/**",
                        "/api/ventas/**", "/api/detalle-venta/**")
                    .hasRole("ADMIN")

                // /auth/validate, /auth/verify y el resto: solo con token válido
                .anyRequest().authenticated()
            )

            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())

            // Sin token o con token inválido -> 401 (el frontend lo usa para volver al login);
            // con token válido pero sin permiso -> 403
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    writeJsonError(response, HttpStatus.UNAUTHORIZED, "No autenticado o token inválido"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    writeJsonError(response, HttpStatus.FORBIDDEN, "Acceso denegado"))
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void writeJsonError(HttpServletResponse response, HttpStatus status, String message)
            throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\",\"data\":null}");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
