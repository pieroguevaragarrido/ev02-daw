package com.lab04.citas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RolFormDTO {

    // Código técnico del rol. Solo se define al registrar; al editar no se modifica.
    @NotBlank(message = "El código del rol es obligatorio")
    @Size(max = 30, message = "El código admite máximo 30 caracteres")
    @Pattern(regexp = "^[A-Za-z_]*$", message = "El código solo admite letras y guion bajo, sin tildes ni espacios")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "La descripción admite máximo 100 caracteres")
    private String descripcion;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
