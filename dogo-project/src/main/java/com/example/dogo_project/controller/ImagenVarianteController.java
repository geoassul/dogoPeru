package com.example.dogo_project.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.dogo_project.dto.DtoMapper;
import com.example.dogo_project.dto.ImagenVarianteDTO;
import com.example.dogo_project.exception.BusinessException;
import com.example.dogo_project.model.ImagenVariante;
import com.example.dogo_project.model.VarianteProducto;
import com.example.dogo_project.response.ApiResponse;
import com.example.dogo_project.service.FileUploadService;
import com.example.dogo_project.service.ImagenVarianteService;
import com.example.dogo_project.service.VarianteProductoService;

@RestController
@RequestMapping("/api/imagenes-variante")
public class ImagenVarianteController {

    @Autowired
    private ImagenVarianteService imagenVarianteService;

    @Autowired
    private VarianteProductoService varianteProductoService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ImagenVarianteDTO>>> getAllImagenes() {
        return ResponseEntity.ok(ApiResponse.success(toDtos(imagenVarianteService.findAll())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ImagenVarianteDTO>> getImagenById(@PathVariable Integer id) {
        return imagenVarianteService.findById(id)
                .map(imagen -> ResponseEntity.ok(ApiResponse.success(DtoMapper.toDto(imagen))))
                .orElseGet(ImagenVarianteController::notFound);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ImagenVarianteDTO>> createImagen(@RequestBody ImagenVarianteDTO imagenDTO) {
        ImagenVariante imagen = new ImagenVariante();
        aplicarCambios(imagen, imagenDTO);
        ImagenVariante saved = imagenVarianteService.save(imagen);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Imagen creada exitosamente", DtoMapper.toDto(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ImagenVarianteDTO>> updateImagen(@PathVariable Integer id,
                                                                       @RequestBody ImagenVarianteDTO imagenDTO) {
        Optional<ImagenVariante> existente = imagenVarianteService.findById(id);
        if (existente.isEmpty()) {
            return notFound();
        }
        ImagenVariante imagen = existente.get();
        aplicarCambios(imagen, imagenDTO);
        ImagenVariante updated = imagenVarianteService.save(imagen);
        return ResponseEntity.ok(ApiResponse.success("Imagen actualizada exitosamente", DtoMapper.toDto(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteImagen(@PathVariable Integer id) {
        Optional<ImagenVariante> imagen = imagenVarianteService.findById(id);
        if (imagen.isEmpty()) {
            return notFound();
        }
        imagenVarianteService.deleteById(id);
        // Si el archivo se subió a este servidor (/uploads/...), también se borra del disco
        fileUploadService.deleteImage(imagen.get().getUrlImagen());
        return ResponseEntity.ok(ApiResponse.success("Imagen eliminada exitosamente", null));
    }

    @GetMapping("/variante/{varianteId}")
    public ResponseEntity<ApiResponse<List<ImagenVarianteDTO>>> getImagenesByVariante(@PathVariable Integer varianteId) {
        return ResponseEntity.ok(ApiResponse.success(toDtos(imagenVarianteService.findByVariante(varianteId))));
    }

    /**
     * Sube una imagen (multipart) y crea su registro.
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ImagenVarianteDTO>> uploadImagen(
            @RequestParam("file") MultipartFile file,
            @RequestParam("varianteId") Integer varianteId,
            @RequestParam("tipoImagen") String tipoImagen,
            @RequestParam(value = "orden", defaultValue = "0") Byte orden,
            @RequestParam(value = "altText", required = false) String altText) throws IOException {
        VarianteProducto variante = buscarVariante(varianteId);

        String urlImagen = fileUploadService.uploadImage(file, "productos");

        ImagenVariante imagen = new ImagenVariante();
        imagen.setVariante(variante);
        imagen.setUrlImagen(urlImagen);
        imagen.setNombreArchivo(file.getOriginalFilename());
        imagen.setTipoImagen(tipoImagen);
        imagen.setOrden(orden);
        imagen.setAltText(altText != null ? altText : "Imagen de " + variante.getSku());

        ImagenVariante saved;
        try {
            saved = imagenVarianteService.save(imagen);
        } catch (RuntimeException e) {
            // No dejar archivos huérfanos en disco si falla el registro en BD
            fileUploadService.deleteImage(urlImagen);
            throw e;
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Imagen subida exitosamente", DtoMapper.toDto(saved)));
    }

    private void aplicarCambios(ImagenVariante imagen, ImagenVarianteDTO dto) {
        if (dto.getUrlImagen() == null || dto.getUrlImagen().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "urlImagen es obligatoria");
        }
        imagen.setVariante(buscarVariante(dto.getVarianteId()));
        imagen.setUrlImagen(dto.getUrlImagen());
        imagen.setTipoImagen(dto.getTipoImagen());
        imagen.setOrden(dto.getOrden());
        if (dto.getNombreArchivo() != null) {
            imagen.setNombreArchivo(dto.getNombreArchivo());
        }
        if (dto.getAltText() != null) {
            imagen.setAltText(dto.getAltText());
        }
    }

    private VarianteProducto buscarVariante(Integer varianteId) {
        if (varianteId == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "varianteId es obligatorio");
        }
        return varianteProductoService.findById(varianteId)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Variante no encontrada"));
    }

    private static List<ImagenVarianteDTO> toDtos(List<ImagenVariante> imagenes) {
        return imagenes.stream().map(DtoMapper::toDto).toList();
    }

    private static <T> ResponseEntity<ApiResponse<T>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Imagen no encontrada"));
    }
}
