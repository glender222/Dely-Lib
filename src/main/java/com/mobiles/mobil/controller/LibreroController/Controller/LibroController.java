package com.mobiles.mobil.controller.LibreroController.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.service.service.AuthService;
import com.mobiles.mobil.service.service.LibroService;

@RestController
@RequestMapping("/api/v1/libros")
public class LibroController {
    
    private final LibroService libroService;
    private final AuthService authService;
    private final LibroRepository  libroRepository;

    public LibroController(LibroService libroService, AuthService authService, LibroRepository libroRepository) {
        this.libroService = libroService;
        this.authService = authService;
        this.libroRepository = libroRepository;
    }

    // CREATE - Solo EMPRESA (gestión de ficha bibliográfica)
    @PostMapping
    public ResponseEntity<LibroDTO> create(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody LibroDTO libroDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        LibroDTO created = libroService.create(libroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL - Ambos roles (catálogo para CLIENTE, gestión para EMPRESA)
    @GetMapping
    public ResponseEntity<List<LibroDTO>> findAll(
            @RequestHeader("X-Session-Id") String sessionId) throws ServiceException {
        
        authService.validar(sessionId); // Solo valida sesión activa
        List<LibroDTO> libros = libroService.findAll();
        return ResponseEntity.ok(libros);
    }

    // READ BY ID - Ambos roles
    @GetMapping("/{id}")
    public ResponseEntity<LibroDTO> findById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        authService.validar(sessionId); // Solo valida sesión activa
        LibroDTO libro = libroService.findById(id);
        return ResponseEntity.ok(libro);
    }

    // UPDATE - Solo EMPRESA (modificar ficha bibliográfica)
    @PutMapping("/{id}")
    public ResponseEntity<LibroDTO> update(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id,
            @RequestBody LibroDTO libroDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        LibroDTO updated = libroService.update(id, libroDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Solo EMPRESA + validar relaciones
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        libroService.deleteById(id); // Aquí se validan todas las reglas de negocio
        return ResponseEntity.noContent().build();
    }


@PostMapping("/{id}/imagen")
public ResponseEntity<String> uploadImage(
        @RequestHeader("X-Session-Id") String sessionId,
        @PathVariable Long id,
        @RequestParam("file") MultipartFile file) {
    try {
        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 1. Guardar archivo en carpeta local
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 2. Actualizar la entidad Libro con la ruta
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con id " + id));
        libro.setImagenPortada(filePath.toString());
        libroRepository.save(libro);

        return ResponseEntity.ok("Imagen subida en: " + filePath.toString());

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error subiendo imagen: " + e.getMessage());
    }
}



}
