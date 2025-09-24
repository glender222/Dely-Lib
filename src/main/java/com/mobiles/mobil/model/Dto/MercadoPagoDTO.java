package com.mobiles.mobil.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MercadoPagoDTO {
    private String preferenceId;
    private String initPoint; // URL para redirección
    private Double totalAmount;
    private String status;
    private String externalReference; // ID de tu compra
}