package com.mobiles.mobil.controller.LibreroController.Controller;

import java.util.Map;

import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mobiles.mobil.model.Dto.MercadoPagoDTO;
import com.mobiles.mobil.model.entity.Usuario;
import com.mobiles.mobil.service.impl.CompraServiceImpl;
import com.mobiles.mobil.service.impl.DetalleCompraServiceImpl;
import com.mobiles.mobil.service.service.AuthService;
import com.mobiles.mobil.service.service.MercadoPagoService;

@RestController
@RequestMapping("/api/v1/mercadopago")
public class MercadoPagoController {
    
    private final MercadoPagoService mercadoPagoService;
    private final CompraServiceImpl compraService;
    private final DetalleCompraServiceImpl detalleCompraService;
    private final AuthService authService;

    public MercadoPagoController(MercadoPagoService mercadoPagoService, 
                                CompraServiceImpl compraService,
                                DetalleCompraServiceImpl detalleCompraService,
                                AuthService authService) {
        this.mercadoPagoService = mercadoPagoService;
        this.compraService = compraService;
        this.detalleCompraService = detalleCompraService;
        this.authService = authService;
    }

    // Crear preferencia de pago - Solo CLIENTE
    @PostMapping("/create-preference")
    public ResponseEntity<?> createPreference(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody Map<String, Long> request) throws ServiceException {
        
        try {
            Usuario usuario = authService.validar(sessionId);
            
            // Solo clientes pueden crear preferencias de pago
            if (!"CLIENTE".equals(usuario.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo clientes pueden crear pagos");
            }
            
            Long compraId = request.get("compraId");
            if (compraId == null) {
                return ResponseEntity.badRequest().body("compraId es requerido");
            }
            
            // Verificar que la compra existe y pertenece al usuario
            var compra = compraService.findById(compraId);
            if (!usuario.getIdUsuario().equals(compra.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para esta compra");
            }
            
            // Calcular total de la compra
            var detalles = detalleCompraService.findByCompraId(compraId);
            double total = detalles.stream()
                .mapToDouble(d -> d.getSubtotal())
                .sum();
            
            if (total <= 0) {
                return ResponseEntity.badRequest().body("El total de la compra debe ser mayor a 0");
            }
            
            // Crear preferencia en Mercado Pago
            MercadoPagoDTO preference = mercadoPagoService.createPreference(compraId, total);
            
            return ResponseEntity.ok(preference);
            
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno: " + e.getMessage());
        }
    }

    // Webhook para recibir notificaciones de Mercado Pago (público)
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("=== MERCADO PAGO WEBHOOK ===");
            System.out.println("Payload recibido: " + payload);
            
            // Extraer información del webhook
            String type = (String) payload.get("type");
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            
            if ("payment".equals(type) && data != null) {
                String paymentId = (String) data.get("id");
                System.out.println("Payment ID: " + paymentId);
                
                // Aquí podrías verificar el pago con Mercado Pago
                // y actualizar el estado de la compra
                
                // Por ahora solo logueamos
                System.out.println("Pago procesado exitosamente: " + paymentId);
            }
            
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            System.err.println("Error procesando webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing webhook");
        }
    }

    // Obtener estado del pago
    @GetMapping("/payment-status/{compraId}")
    public ResponseEntity<?> getPaymentStatus(
            @RequestHeader("X-Session-Id") String sessionId,
            @PathVariable Long compraId) throws ServiceException {
        
        try {
            Usuario usuario = authService.validar(sessionId);
            
            // Verificar permisos
            var compra = compraService.findById(compraId);
            
            if ("CLIENTE".equals(usuario.getRol()) && !usuario.getIdUsuario().equals(compra.getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para ver esta compra");
            }
            
            return ResponseEntity.ok(Map.of(
                "compraId", compraId,
                "estado", compra.getEstadoProcesoCompra(),
                "fechaPago", compra.getFechaPago() != null ? compra.getFechaPago() : "Pendiente"
            ));
            
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage());
        }
    }
}