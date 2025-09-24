package com.mobiles.mobil.service.impl;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mobiles.mobil.model.Dto.CompraDTO;
import com.mobiles.mobil.model.Dto.DetalleCompraDTO;
import com.mobiles.mobil.model.entity.Carrito;
import com.mobiles.mobil.model.entity.Compra;
import com.mobiles.mobil.model.entity.DetalleCompra;
import com.mobiles.mobil.model.entity.Inventario;
import com.mobiles.mobil.model.mapper.CompraMapper;
import com.mobiles.mobil.repository.CarritoRepository;
import com.mobiles.mobil.repository.CompraRepository;
import com.mobiles.mobil.repository.DetalleCompraRepository;
import com.mobiles.mobil.repository.InventarioRepository;
import com.mobiles.mobil.repository.UsuarioRepository;
import com.mobiles.mobil.service.service.CompraService;

@Service
public class CompraServiceImpl implements CompraService {
    private final CompraMapper compraMapper;
    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final InventarioRepository inventarioRepository;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CompraServiceImpl(CompraMapper compraMapper, CompraRepository compraRepository,
                            UsuarioRepository usuarioRepository, CarritoRepository carritoRepository,
                            DetalleCompraRepository detalleCompraRepository, InventarioRepository inventarioRepository) {
        this.compraMapper = compraMapper;
        this.compraRepository = compraRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoRepository = carritoRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    @Transactional
    public CompraDTO create(CompraDTO dto) throws ServiceException {
        try {
            // 1. Validar que el usuario existe
            if (!usuarioRepository.existsById(dto.getIdUsuario())) {
                throw new ServiceException("Usuario not found with id: " + dto.getIdUsuario());
            }
            
            // 2. Obtener items del carrito del usuario
            List<Carrito> carritoItems = carritoRepository.findByUsuarioIdUsuario(dto.getIdUsuario());
            if (carritoItems.isEmpty()) {
                throw new ServiceException("Cannot create purchase: carrito is empty");
            }
            
            // 3. Validar stock para todos los items antes de proceder
            for (Carrito item : carritoItems) {
                Optional<Inventario> inventarioOpt = inventarioRepository.findByLibroIdLibro(
                    item.getLibro().getIdLibro());
                
                if (inventarioOpt.isEmpty()) {
                    throw new ServiceException("No inventory found for libro: " + item.getLibro().getTitulo());
                }
                
                Inventario inventario = inventarioOpt.get();
                if (inventario.getCantidadStock() < item.getCantidad()) {
                    throw new ServiceException("Insufficient stock for libro: " + item.getLibro().getTitulo() + 
                                             ". Available: " + inventario.getCantidadStock() + 
                                             ", required: " + item.getCantidad());
                }
            }
            
            // 4. Crear la compra
            dto.setEstadoProcesoCompra("PAGADO"); // Estado inicial después del pago
            dto.setFechaPago(LocalDateTime.now().format(FORMATTER));
            
            Compra compra = compraMapper.toEntity(dto);
            Compra savedCompra = compraRepository.save(compra);
            
            // 5. Crear detalles de compra y actualizar inventario
            for (Carrito item : carritoItems) {
                // Crear detalle
                DetalleCompra detalle = new DetalleCompra();
                detalle.setCompra(savedCompra);
                detalle.setLibro(item.getLibro());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecioUnitario());
                detalle.setSubtotal(item.getCantidad() * item.getPrecioUnitario());
                detalleCompraRepository.save(detalle);
                
                // Actualizar stock
                Inventario inventario = inventarioRepository.findByLibroIdLibro(
                    item.getLibro().getIdLibro()).get();
                inventario.setCantidadStock(inventario.getCantidadStock() - item.getCantidad());
                inventarioRepository.save(inventario);
            }
            
            // 6. Limpiar carrito después de la compra exitosa
            carritoRepository.deleteByUsuarioIdUsuario(dto.getIdUsuario());
            
            return compraMapper.toDTO(savedCompra);
        } catch (Exception e) {
            throw new ServiceException("Error creating compra: " + e.getMessage());
        }
    }

    @Override
    public CompraDTO update(Long id, CompraDTO dto) throws ServiceException {
        try {
            Optional<Compra> existingOpt = compraRepository.findById(id);
            if (existingOpt.isEmpty()) {
                throw new ServiceException("Compra not found with id: " + id);
            }
            
            Compra existing = existingOpt.get();
            
            // Solo permitir actualización de ciertos campos
            if (dto.getDireccionEnvio() != null) {
                existing.setDireccionEnvio(dto.getDireccionEnvio());
            }
            if (dto.getDistrito() != null) {
                existing.setDistrito(dto.getDistrito());
            }
            if (dto.getCalle() != null) {
                existing.setCalle(dto.getCalle());
            }
            if (dto.getCiudad() != null) {
                existing.setCiudad(dto.getCiudad());
            }
            
            Compra updated = compraRepository.save(existing);
            return compraMapper.toDTO(updated);
        } catch (Exception e) {
            throw new ServiceException("Error updating compra: " + e.getMessage());
        }
    }

    @Override
    public CompraDTO findById(Long id) throws ServiceException {
        try {
            Optional<Compra> compra = compraRepository.findById(id);
            if (compra.isEmpty()) {
                throw new ServiceException("Compra not found with id: " + id);
            }
            return compraMapper.toDTO(compra.get());
        } catch (Exception e) {
            throw new ServiceException("Error finding compra: " + e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) throws ServiceException {
        try {
            Optional<Compra> compraOpt = compraRepository.findById(id);
            if (compraOpt.isEmpty()) {
                throw new ServiceException("Compra not found with id: " + id);
            }
            
            Compra compra = compraOpt.get();
            
            // Solo permitir eliminar si está en estado PAGADO
            if (!"PAGADO".equals(compra.getEstadoProcesoCompra())) {
                throw new ServiceException("Cannot delete compra: only PAGADO purchases can be cancelled");
            }
            
            // Restaurar stock antes de eliminar
            List<DetalleCompra> detalles = detalleCompraRepository.findByCompraIdCompra(id);
            for (DetalleCompra detalle : detalles) {
                Optional<Inventario> inventarioOpt = inventarioRepository.findByLibroIdLibro(
                    detalle.getLibro().getIdLibro());
                if (inventarioOpt.isPresent()) {
                    Inventario inventario = inventarioOpt.get();
                    inventario.setCantidadStock(inventario.getCantidadStock() + detalle.getCantidad());
                    inventarioRepository.save(inventario);
                }
            }
            
            compraRepository.deleteById(id);
        } catch (Exception e) {
            throw new ServiceException("Error deleting compra: " + e.getMessage());
        }
    }

    @Override
    public List<CompraDTO> findAll() throws ServiceException {
        try {
            List<Compra> entities = compraRepository.findAll();
            return compraMapper.toDTOs(entities);
        } catch (Exception e) {
            throw new ServiceException("Error finding all compras: " + e.getMessage());
        }
    }

    // MÉTODO ADICIONAL: Buscar compras por usuario
    public List<CompraDTO> findByUsuarioId(Long usuarioId) throws ServiceException {
        try {
            if (!usuarioRepository.existsById(usuarioId)) {
                throw new ServiceException("Usuario not found with id: " + usuarioId);
            }
            
            List<Compra> compras = compraRepository.findByUsuarioIdUsuario(usuarioId);
            return compraMapper.toDTOs(compras);
        } catch (Exception e) {
            throw new ServiceException("Error finding compras by usuario: " + e.getMessage());
        }
    }

    // MÉTODO ADICIONAL: Actualizar estado de compra (solo para EMPRESA)
    @Transactional
    public CompraDTO updateEstado(Long compraId, String nuevoEstado) throws ServiceException {
        try {
            Optional<Compra> compraOpt = compraRepository.findById(compraId);
            if (compraOpt.isEmpty()) {
                throw new ServiceException("Compra not found with id: " + compraId);
            }
            
            Compra compra = compraOpt.get();
            String estadoActual = compra.getEstadoProcesoCompra();
            
            // Validar transiciones válidas
            if (!isValidTransition(estadoActual, nuevoEstado)) {
                throw new ServiceException("Invalid state transition from " + estadoActual + " to " + nuevoEstado);
            }
            
            compra.setEstadoProcesoCompra(nuevoEstado);
            
            // Actualizar fechas según el estado
            String now = LocalDateTime.now().format(FORMATTER);
            switch (nuevoEstado) {
                case "ENVIADO":
                    compra.setFechaCreacionEmpaquetado(now);
                    break;
                case "ENTREGADO":
                    compra.setFechaEntrega(now);
                    break;
            }
            
            Compra updated = compraRepository.save(compra);
            return compraMapper.toDTO(updated);
        } catch (Exception e) {
            throw new ServiceException("Error updating estado: " + e.getMessage());
        }
    }

    private boolean isValidTransition(String estadoActual, String nuevoEstado) {
        return switch (estadoActual) {
            case "PAGADO" -> "ENVIADO".equals(nuevoEstado);
            case "ENVIADO" -> "ENTREGADO".equals(nuevoEstado);
            default -> false;
        };
    }
}