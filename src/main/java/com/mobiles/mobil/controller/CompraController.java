package com.mobiles.mobil.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobiles.mobil.controller.LoginController.LoginHeaders;
import com.mobiles.mobil.model.Dto.CompraDTO;
import com.mobiles.mobil.model.Dto.DetalleCompraDTO;
import com.mobiles.mobil.model.entity.Carrito;
import com.mobiles.mobil.model.entity.Compra;
import com.mobiles.mobil.model.entity.DetalleCompra;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.model.mapper.CompraMapper;
import com.mobiles.mobil.model.mapper.DetalleCompraMapper;
import com.mobiles.mobil.repository.CarritoRepository;
import com.mobiles.mobil.repository.CompraRepository;
import com.mobiles.mobil.repository.DetalleCompraRepository;
import com.mobiles.mobil.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {
    private final AuthService authService;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final CarritoRepository carritoRepository;
    private final CompraMapper compraMapper;
    private final DetalleCompraMapper detalleCompraMapper;

    public CompraController(AuthService authService,
                            CompraRepository compraRepository,
                            DetalleCompraRepository detalleCompraRepository,
                            CarritoRepository carritoRepository,
                            CompraMapper compraMapper,
                            DetalleCompraMapper detalleCompraMapper) {
        this.authService = authService;
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.carritoRepository = carritoRepository;
        this.compraMapper = compraMapper;
        this.detalleCompraMapper = detalleCompraMapper;
    }

    // Confirmar compra
    @PostMapping
    public ResponseEntity<CompraDTO> checkout(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @RequestBody CompraDTO dto) {
        Usuario usuario = authService.validar(sessionId);

        Compra compra = Compra.builder()
                .usuario(usuario)
                .direccionEnvio(dto.getDireccionEnvio())
                .distrito(dto.getDistrito())
                .calle(dto.getCalle())
                .ciudad(dto.getCiudad())
                .fechaPago(dto.getFechaPago())
                .fechaCreacionEmpaquetado(dto.getFechaCreacionEmpaquetado())
                .fechaEntrega(dto.getFechaEntrega())
                .estadoProcesoCompra(dto.getEstadoProcesoCompra())
                .build();
        Compra saved = compraRepository.save(compra);

        List<Carrito> items = carritoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
        for (Carrito item : items) {
            DetalleCompra det = DetalleCompra.builder()
                    .compra(saved)
                    .libro(item.getLibro())
                    .cantidad(item.getCantidad())
                    .precioUnitario(item.getPrecioUnitario())
                    .subtotal(item.getPrecioUnitario() * item.getCantidad())
                    .build();
            detalleCompraRepository.save(det);
        }
        // Vaciar carrito luego de generar la compra
        carritoRepository.deleteByUsuarioIdUsuario(usuario.getIdUsuario());

        return ResponseEntity.status(HttpStatus.CREATED).body(compraMapper.toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<CompraDTO>> myOrders(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId) {
        Usuario usuario = authService.validar(sessionId);
        List<Compra> compras = compraRepository.findAll() // simple: filtra manualmente
                .stream().filter(c -> c.getUsuario().getIdUsuario().equals(usuario.getIdUsuario()))
                .toList();
        return ResponseEntity.ok(compraMapper.toDTOs(compras));
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleCompraDTO>> detalles(
            @RequestHeader(LoginHeaders.SESSION_HEADER) String sessionId,
            @PathVariable Long id) {
        Usuario usuario = authService.validar(sessionId);
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
        if (!compra.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<DetalleCompra> detalles = detalleCompraRepository.findAll().stream()
                .filter(d -> d.getCompra().getIdCompra().equals(id))
                .toList();
        return ResponseEntity.ok(detalleCompraMapper.toDTOs(detalles));
    }
}


