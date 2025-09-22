package com.mobiles.mobil.model.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DetalleCompraDTO {
    private Long idDetalleCompra;
    private Long idCompra;
    private Long idLibro;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}

