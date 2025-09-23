package com.mobiles.mobil.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long>{
    boolean existsByLibroIdLibro(Long idLibro);

    java.util.List<Carrito> findByUsuarioIdUsuario(Long idUsuario);
    void deleteByUsuarioIdUsuario(Long idUsuario);
}
