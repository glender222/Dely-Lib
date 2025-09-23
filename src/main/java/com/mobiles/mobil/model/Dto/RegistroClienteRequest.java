package com.mobiles.mobil.model.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistroClienteRequest {

    @NotBlank(message = "El nombre completo es requerido")
    private String nombreCompleto;

    @NotBlank(message = "El email es requerido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    private String password;

    private String fechaNacimiento;
}
