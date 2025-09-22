package com.mobiles.mobil.service.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobiles.mobil.model.Dto.LoginRequest;
import com.mobiles.mobil.model.Dto.LoginResponse;
import com.mobiles.mobil.model.Dto.RegistroClienteRequest;
import com.mobiles.mobil.model.entity.Sesion;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.repository.SesionRepository;
import com.mobiles.mobil.repository.UsuarioRepository;

@Service
public class AuthService {
 private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;

    public AuthService(UsuarioRepository usuarioRepository, SesionRepository sesionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.sesionRepository = sesionRepository;
    }


/** Registro de CLIENTE */
    @Transactional
    public Long registrarCliente(RegistroClienteRequest req) {
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        Usuario u = new Usuario();
        u.setNombreCompleto(req.getNombreCompleto());
        u.setEmail(req.getEmail());
        u.setPassword(req.getPassword()); // texto plano por simplicidad
        u.setRol("CLIENTE");
        u.setEstado("ACTIVO");
        u.setFechaNacimiento(req.getFechaNacimiento());
        usuarioRepository.save(u);
        return u.getIdUsuario();
    }

    /** Login básico: crea sesión y devuelve datos */
    @Transactional
    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarioRepository.findByEmailAndPassword(req.getEmail(), req.getPassword())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        Sesion s = new Sesion();
        s.setIdSesion(UUID.randomUUID().toString());
        s.setUsuario(u);
        s.setExpiraEn(LocalDateTime.now().plusDays(7)); // opcional
        s.setActivo("1");
        sesionRepository.save(s);

        LoginResponse res = new LoginResponse();
        res.setSessionId(s.getIdSesion());
        res.setUserId(u.getIdUsuario());
        res.setNombre(u.getNombreCompleto());
        res.setRol(u.getRol());
        return res;
    }

    /** Logout: desactiva la sesión */
    @Transactional
    public void logout(String sessionId) {
        Optional<Sesion> s = sesionRepository.findById(sessionId);
        s.ifPresent(sess -> {
            sess.setActivo("0");
            sesionRepository.save(sess);
        });
    }

    /** Valida sesión y devuelve el usuario */
    @Transactional(readOnly = true)
    public Usuario validar(String sessionId) {
        Sesion s = sesionRepository.findByIdSesionAndActivo(sessionId, "1")
                .orElseThrow(() -> new RuntimeException("Sesión inválida o expirada"));
        if (s.getExpiraEn() != null && s.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Sesión expirada");
        }
        return s.getUsuario();
    }

}
