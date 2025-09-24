package com.mobiles.mobil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long>{
  // Método para verificar si un libro está en carritos (DELETE validation)
    boolean existsByLibroIdLibro(Long idLibro);

        // Métodos adicionales para CarritoService
    List<Carrito> findByUsuarioIdUsuario(Long idUsuario);
    Optional<Carrito> findByUsuarioIdUsuarioAndLibroIdLibro(Long idUsuario, Long idLibro);
    void deleteByUsuarioIdUsuario(Long idUsuario);
}
