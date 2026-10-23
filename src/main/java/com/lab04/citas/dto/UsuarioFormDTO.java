package com.lab04.citas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioFormDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "El usuario solo admite letras, números, punto, guion y guion bajo")
    private String username;

    // Obligatoria al registrar; al editar, vacía = conservar la actual (se valida en el servicio)
    private String password;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Los nombres admiten máximo 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos admiten máximo 100 caracteres")
    private String apellidos;

    @NotNull(message = "Debe seleccionar un rol")
    private Long rolId;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public Long getRolId() { return rolId; }
    public void setRolId(Long rolId) { this.rolId = rolId; }
}
