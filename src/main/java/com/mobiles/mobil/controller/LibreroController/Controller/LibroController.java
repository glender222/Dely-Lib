package com.mobiles.mobil.controller.LibreroController.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mobiles.mobil.model.Dto.LibroDTO;
import com.mobiles.mobil.model.entity.Libro;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.repository.LibroRepository;
import com.mobiles.mobil.controller.LoginController.LoginHeaders;
import com.mobiles.mobil.service.service.AuthService;
import com.mobiles.mobil.service.service.LibroService;

@RestController
@RequestMapping("/api/v1/libros")
public class LibroController {

    private final LibroService libroService;
    private final AuthService authService;
    private final LibroRepository libroRepository;

    public LibroController(LibroService libroService, AuthService authService, LibroRepository libroRepository) {
        this.libroService = libroService;
        this.authService = authService;
        this.libroRepository = libroRepository;
    }

    // CREATE - Solo EMPRESA
    @PostMapping
    public ResponseEntity<LibroDTO> create(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @RequestBody LibroDTO libroDTO) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Sugerencia: si puntuacionPromedio viene null desde la app, tu servicio puede
        // inicializarla a 0.0 internamente si lo deseas.
        LibroDTO created = libroService.create(libroDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL - Ambos roles
    @GetMapping
    public ResponseEntity<List<LibroDTO>> findAll(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId) throws ServiceException {

        authService.validar(sessionId);
        List<LibroDTO> libros = libroService.findAll();
        return ResponseEntity.ok(libros);
    }

    // READ BY ID - Ambos roles
    @GetMapping("/{id}")
    public ResponseEntity<LibroDTO> findById(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id) throws ServiceException {

        authService.validar(sessionId);
        LibroDTO libro = libroService.findById(id);
        return ResponseEntity.ok(libro);
    }

    // UPDATE - Solo EMPRESA
    @PutMapping("/{id}")
    public ResponseEntity<LibroDTO> update(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id,
            @RequestBody LibroDTO libroDTO) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LibroDTO updated = libroService.update(id, libroDTO);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Solo EMPRESA
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id) throws ServiceException {

        Usuario usuario = authService.validar(sessionId);
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        libroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // SUBIR IMAGEN - Solo EMPRESA
    @PostMapping("/{id}/imagen")
    public ResponseEntity<String> uploadImage(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Usuario usuario = authService.validar(sessionId);
            if (!"EMPRESA".equals(usuario.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 1) Asegurar carpeta uploads
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 2) Generar nombre único y preservar extensión
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String storedFileName = "libro_" + id + "_" + System.currentTimeMillis() + ext;

            // 3) Guardar archivo
            Path filePath = uploadDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4) Actualizar entidad Libro: guardar SOLO el nombre del archivo
            Libro libro = libroRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado con id " + id));
            libro.setImagenPortada(storedFileName);
            libroRepository.save(libro);

            // 5) Construir URL pública desde el contexto actual (sin hardcodear host/puerto)
            String publicUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/libros/")
                    .path(id.toString())
                    .path("/imagen")
                    .toUriString();

            return ResponseEntity.ok("Imagen subida: " + publicUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error subiendo imagen: " + e.getMessage());
        }
    }

    // SERVIR IMAGEN - Ambos roles
    @GetMapping("/{id}/imagen")
    public ResponseEntity<ByteArrayResource> getImage(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id) {
        try {
            authService.validar(sessionId);

            Libro libro = libroRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado con id " + id));

            // Ahora imagenPortada guarda el NOMBRE del archivo (no una URL)
            String fileName = libro.getImagenPortada();
            if (fileName == null || fileName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            Path filePath = Paths.get("uploads").resolve(fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] bytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            MediaType mediaType = (contentType != null)
                    ? MediaType.parseMediaType(contentType)
                    : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(new ByteArrayResource(bytes));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}