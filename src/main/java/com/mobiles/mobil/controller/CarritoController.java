package com.mobiles.mobil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobiles.mobil.controller.LoginController.LoginHeaders;
import com.mobiles.mobil.model.Dto.CarritoDTO;
import com.mobiles.mobil.model.entity.Carrito;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.model.mapper.CarritoMapper;
import com.mobiles.mobil.repository.CarritoRepository;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {
    private final AuthService authService;
    private final CarritoRepository carritoRepository;
    private final LibroRepository libroRepository;
    private final CarritoMapper carritoMapper;

    public CarritoController(AuthService authService, CarritoRepository carritoRepository,
                             LibroRepository libroRepository, CarritoMapper carritoMapper) {
        this.authService = authService;
        this.carritoRepository = carritoRepository;
        this.libroRepository = libroRepository;
        this.carritoMapper = carritoMapper;
    }

    @PostMapping
    public ResponseEntity<CarritoDTO> addItem(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @RequestBody CarritoDTO dto) {
        Usuario usuario = authService.validar(sessionId);
        Libro libro = libroRepository.findById(dto.getIdLibro())
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        Carrito entity = Carrito.builder()
                .usuario(usuario)
                .libro(libro)
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : 0.0)
                .build();

        Carrito saved = carritoRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoMapper.toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<CarritoDTO>> myCart(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId) {
        Usuario usuario = authService.validar(sessionId);
        List<Carrito> items = carritoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        return ResponseEntity.ok(carritoMapper.toDTOs(items));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeItem(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id) {
        Usuario usuario = authService.validar(sessionId);
        Carrito item = carritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));
        if (!item.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        carritoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId) {
        Usuario usuario = authService.validar(sessionId);
        carritoRepository.deleteByUsuarioIdUsuario(usuario.getIdUsuario());
        return ResponseEntity.noContent().build();
    }
}


