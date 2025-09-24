package com.mobiles.mobil.service.service;

import org.springframework.stereotype.Service;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {
    
    public MercadoPagoService() {
        // Configurar token de acceso (desde application.properties)
        MercadoPagoConfig.setAccessToken("TU_ACCESS_TOKEN_AQUI");
    }
    
    public String createPreference(Long compraId, Double totalAmount) throws MPException {
        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
            .title("Compra de libros - Dely-Lib")
            .quantity(1)
            .unitPrice(new BigDecimal(totalAmount))
            .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .items(items)
            .externalReference(compraId.toString()) // Para identificar la compra
            .notificationUrl("https://tudominio.com/api/v1/pagos/webhook") // URL webhook
            .build();
       
        PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);  // vota error

        
        return preference.getInitPoint(); // URL para redireccionar
    }
}
