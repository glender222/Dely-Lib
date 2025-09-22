package com.mobiles.mobil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{



      Optional<Usuario> findByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
}
