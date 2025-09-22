package com.mobiles.mobil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mobiles.mobil.model.entity.Sesion;

public interface SesionRepository extends JpaRepository <Sesion, String> {
    Optional<Sesion> findByIdSesionAndActivo(String idSesion, String activo);

}
