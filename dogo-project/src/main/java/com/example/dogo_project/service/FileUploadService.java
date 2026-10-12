package com.example.dogo_project.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    /** Tipos permitidos y la extensión con la que se guardan. SVG queda fuera: puede contener JavaScript. */
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", ".jpg",
            "image/jpg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp");

    @Value("${app.upload.dir:${user.home}/dogo-uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size:10485760}") // 10MB default
    private long maxFileSize;

    public String uploadImage(MultipartFile file, String categoria) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("El archivo es demasiado grande");
        }
        if (!categoria.matches("[a-z0-9_-]+")) {
            throw new IllegalArgumentException("Categoría de carpeta inválida");
        }

        // El Content-Type lo envía el cliente: además de la lista blanca se revisan los bytes iniciales.
        // La extensión sale del tipo validado, nunca del nombre original (evita subir .html, .svg, .jsp...)
        String contentType = file.getContentType();
        String extension = contentType != null ? EXTENSIONES.get(contentType.toLowerCase()) : null;
        if (extension == null) {
            throw new IllegalArgumentException("Solo se permiten imágenes JPG, PNG, GIF o WEBP");
        }
        try (InputStream in = file.getInputStream()) {
            if (!esImagenValida(in.readNBytes(12), extension)) {
                throw new IllegalArgumentException("El contenido del archivo no corresponde a una imagen válida");
            }
        }

        Path uploadPath = Paths.get(uploadDir, categoria);
        Files.createDirectories(uploadPath);

        String filename = UUID.randomUUID() + extension;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + categoria + "/" + filename;
    }

    public boolean deleteImage(String urlImagen) {
        if (urlImagen == null || !urlImagen.startsWith("/uploads/")) {
            return false;
        }
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = base.resolve(urlImagen.substring("/uploads/".length())).normalize();
            // Impide borrar fuera de la carpeta de uploads con rutas tipo /uploads/../../archivo
            if (!filePath.startsWith(base)) {
                return false;
            }
            return Files.deleteIfExists(filePath);
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    /** Comprueba la firma ("magic bytes") del archivo según el tipo declarado. */
    private static boolean esImagenValida(byte[] h, String extension) {
        return switch (extension) {
            case ".jpg" -> h.length >= 3 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF;
            case ".png" -> h.length >= 8 && Arrays.equals(Arrays.copyOf(h, 8),
                    new byte[] {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'});
            case ".gif" -> h.length >= 6 && new String(h, 0, 6).matches("GIF8[79]a");
            case ".webp" -> h.length >= 12 && new String(h, 0, 4).equals("RIFF") && new String(h, 8, 4).equals("WEBP");
            default -> false;
        };
    }
}
