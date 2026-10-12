package com.example.dogo_project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagenes_variante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImagenVariante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variante_id")
    private VarianteProducto variante;

    @Column(name = "url_imagen", length = 500)
    private String urlImagen;

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "tipo_imagen", length = 20)
    private String tipoImagen; // principal, galeria, detalle

    @Column(name = "orden")
    private Byte orden;

    @Column(name = "alt_text", length = 255)
    private String altText; // Para SEO y accesibilidad
}
