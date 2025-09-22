package com.mobiles.mobil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long>{


    // Método para verificar si un libro tiene inventario (DELETE validation)
    boolean existsByLibroIdLibro(Long idLibro);

     // Para buscar inventario por libro específico (ENDPOINT adicional)
    Optional<Inventario> findByLibroIdLibro(Long idLibro);
}
