package com.mobiles.mobil.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.DetalleCompra;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long>{
 // Método para verificar si un libro tiene historial de compras (DELETE validation)
    boolean existsByLibroIdLibro(Long idLibro);
}
