package com.mobiles.mobil.service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.mobiles.mobil.model.Dto.MercadoPagoDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {
    
    @Value("${mercadopago.access.token}")
    private String accessToken;
    
    @Value("${mercadopago.webhook.base.url:http://localhost:9090}")
    private String webhookBaseUrl;

    public MercadoPagoDTO createPreference(Long compraId, Double totalAmount) {
        try {
            // Configurar token cada vez (mejor práctica)
            MercadoPagoConfig.setAccessToken(accessToken);
            
            // Crear item de la compra
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title("Compra de libros - Dely-Lib #" + compraId)
                .quantity(1)
                .unitPrice(new BigDecimal(totalAmount.toString()))
                .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // Construir webhook URL dinámicamente
            String webhookUrl = webhookBaseUrl + "/api/v1/mercadopago/webhook";
            
            // Crear preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .externalReference(compraId.toString()) // Para identificar la compra
                .notificationUrl(webhookUrl) // URL webhook dinámica
                .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // Crear DTO de respuesta
            MercadoPagoDTO dto = new MercadoPagoDTO();
            dto.setPreferenceId(preference.getId());
            dto.setInitPoint(preference.getInitPoint());
            dto.setTotalAmount(totalAmount);
            dto.setStatus("pending");
            dto.setExternalReference(compraId.toString());
            
            System.out.println("=== PREFERENCIA CREADA ===");
            System.out.println("Preference ID: " + preference.getId());
            System.out.println("Init Point: " + preference.getInitPoint());
            System.out.println("Webhook URL: " + webhookUrl);
            
            return dto;
            
        } catch (MPApiException e) {
            // Manejo específico de errores de API de Mercado Pago
            System.err.println("Error de API de Mercado Pago: " + e.getApiResponse().getContent());
            throw new RuntimeException("Error creating Mercado Pago preference: " + e.getMessage(), e);
        } catch (MPException e) {
            // Manejo de otras excepciones de Mercado Pago
            System.err.println("Error de Mercado Pago: " + e.getMessage());
            throw new RuntimeException("Error with Mercado Pago service: " + e.getMessage(), e);
        } catch (Exception e) {
            // Manejo de errores generales
            System.err.println("Error general: " + e.getMessage());
            throw new RuntimeException("Unexpected error creating payment preference: " + e.getMessage(), e);
        }
    }

    public boolean verifyPayment(String paymentId) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);
            // Aquí podrías verificar el estado del pago si fuera necesario
            return true; // Simplificado para este ejemplo
        } catch (Exception e) {
            System.err.println("Error verificando pago: " + e.getMessage());
            return false;
        }
    }
}