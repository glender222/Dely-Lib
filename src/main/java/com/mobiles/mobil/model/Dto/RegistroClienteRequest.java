package com.mobiles.mobil.model.Dto;

import lombok.Data;

@Data
public class RegistroClienteRequest {
   private String nombreCompleto;
    private String email;
    private String password;
    private String fechaNacimiento; // opcional
}
