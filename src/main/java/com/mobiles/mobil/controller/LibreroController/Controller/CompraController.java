package com.mobiles.mobil.controller.LibreroController.Controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.mobiles.mobil.model.Dto.CompraDTO;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.service.impl.CompraServiceImpl;
import com.mobiles.mobil.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {
    
    private final CompraServiceImpl compraService;
    private final AuthService authService;

    public CompraController(CompraServiceImpl compraService, AuthService authService) {
        this.compraService = compraService;
        this.authService = authService;
    }

    // CREATE - Solo CLIENTE puede crear compras
    @PostMapping
    public ResponseEntity<CompraDTO> create(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody CompraDTO compraDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        
        if (!"CLIENTE".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Asegurar que está creando SU compra
        compraDTO.setIdUsuario(usuario.getIdUsuario());
        
        CompraDTO created = compraService.create(compraDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ ALL - CLIENTE ve sus compras, EMPRESA ve todas
    @GetMapping
    public ResponseEntity<List<CompraDTO>> findCompras(
            @RequestHeader("X-Session-Id") String sessionId) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        List<CompraDTO> compras;
        
        if ("CLIENTE".equals(usuario.getRol())) {
            // Cliente solo ve sus compras
            compras = compraService.findByUsuarioId(usuario.getIdUsuario());
        } else if ("EMPRESA".equals(usuario.getRol())) {
            // Empresa ve todas las compras
            compras = compraService.findAll();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(compras);
    }

    // READ BY ID - Ambos roles con validaciones
    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> findById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        CompraDTO compra = compraService.findById(id);
        
        // Cliente solo puede ver sus propias compras
        if ("CLIENTE".equals(usuario.getRol()) && !usuario.getIdUsuario().equals(compra.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(compra);
    }

    // READ - Compras de usuario específico (solo EMPRESA)
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<CompraDTO>> findComprasByUsuario(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long userId) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<CompraDTO> compras = compraService.findByUsuarioId(userId);
        return ResponseEntity.ok(compras);
    }

    // UPDATE - Actualizar datos de compra
    @PutMapping("/{id}")
    public ResponseEntity<CompraDTO> update(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id,
            @RequestBody CompraDTO compraDTO) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        CompraDTO existing = compraService.findById(id);
        
        // Cliente solo puede actualizar sus compras y solo si están en PAGADO
        if ("CLIENTE".equals(usuario.getRol())) {
            if (!usuario.getIdUsuario().equals(existing.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (!"PAGADO".equals(existing.getEstadoProcesoCompra())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Error", "Solo se pueden modificar compras en estado PAGADO")
                    .build();
            }
        }
        
        CompraDTO updated = compraService.update(id, compraDTO);
        return ResponseEntity.ok(updated);
    }

    // UPDATE ESTADO - Solo EMPRESA puede cambiar estados
    @PutMapping("/{id}/estado")
    public ResponseEntity<CompraDTO> updateEstado(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id,
            @RequestBody Map<String, String> estadoRequest) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        
        if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        String nuevoEstado = estadoRequest.get("estado");
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .header("Error", "Estado is required")
                .build();
        }
        
        // Validar estados válidos
        if (!List.of("PAGADO", "ENVIADO", "ENTREGADO").contains(nuevoEstado)) {
            return ResponseEntity.badRequest()
                .header("Error", "Invalid estado. Must be: PAGADO, ENVIADO, ENTREGADO")
                .build();
        }
        
        CompraDTO updated = compraService.updateEstado(id, nuevoEstado);
        return ResponseEntity.ok(updated);
    }

    // DELETE - Cancelar compra (solo si está en PAGADO)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long id) throws ServiceException {
        
        Usuario usuario = authService.validar(sessionId);
        CompraDTO existing = compraService.findById(id);
        
        // Solo el cliente dueño o la empresa pueden cancelar
        if ("CLIENTE".equals(usuario.getRol())) {
            if (!usuario.getIdUsuario().equals(existing.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else if (!"EMPRESA".equals(usuario.getRol())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        compraService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}