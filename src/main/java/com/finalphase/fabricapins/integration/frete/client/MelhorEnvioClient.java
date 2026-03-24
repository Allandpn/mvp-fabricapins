package com.finalphase.fabricapins.integration.frete.client;

import com.finalphase.fabricapins.integration.frete.dto.MelhorEnvioResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class MelhorEnvioClient {

    private final WebClient webClient;
    private final MelhorEnvioProperties properties;


    public MelhorEnvioClient(WebClient.Builder builder, MelhorEnvioProperties properties){
        this.properties = properties;
        this.webClient = builder.baseUrl(properties.getUrl()).build();
    }

    public List<MelhorEnvioResponse> calcularFrete(Object request){
        return webClient.post()
                .uri("/shipment/calculate")
                .headers(headers -> {
                    headers.setBearerAuth(properties.getToken());
                    headers.set("User-Agent", properties.getApplication() + " (" + properties.getEmail() + ")");
                })
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(MelhorEnvioResponse.class)
                .collectList()
                .block();
    }

}
