package com.finalphase.fabricapins.integration.frete.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MelhorEnvioClient {

    private final WebClient webClient;

    public MelhorEnvioClient(WebClient.Builder builder){
        this.webClient = builder.baseUrl("https://sandbox.melhorenvio.com.br/api/v2/me").build();
    }

    public String calcularFrete(Object request, String token){
        return webClient.post()
                .uri("/shipment/calculate")
                .header("Authorization", "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
