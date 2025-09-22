package com.mobiles.mobil.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long>{
  // Método para verificar si un libro está en carritos (DELETE validation)
    boolean existsByLibroIdLibro(Long idLibro);
}
