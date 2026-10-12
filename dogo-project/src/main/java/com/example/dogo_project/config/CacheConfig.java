package com.example.dogo_project.config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de cache para la aplicación
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configuración del cache manager
     * Usando ConcurrentMapCacheManager para simplicidad
     * En producción considerar usar Redis o EhCache
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Definir los nombres de cache que se utilizarán
        cacheManager.setCacheNames(Arrays.asList(
            "categorias",
            "proveedores", 
            "productos",
            "variantes",
            "imagenes-variante",
            "ventas-recientes",
            "dashboard-stats"
        ));
        
        return cacheManager;
    }
}
