package com.example.dogo_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProveedorDTO {
    private Integer id;
    
    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(min = 2, max = 30, message = "El nombre debe tener entre 2 y 30 caracteres")
    private String nombre;
    
    @NotBlank(message = "El teléfono es obligatorio")
    // La columna telefono es VARCHAR(10)
    @Pattern(regexp = "^(?=.{7,10}$)[+]?[0-9 ()-]+$", message = "Teléfono inválido: de 7 a 10 caracteres (números, espacios, +, -, paréntesis)")
    private String telefono;

    public ProveedorDTO() {}

    public ProveedorDTO(Integer id, String nombre, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
