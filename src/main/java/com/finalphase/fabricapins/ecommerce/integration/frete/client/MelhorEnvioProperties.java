package com.finalphase.fabricapins.ecommerce.integration.frete.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "melhorenvio")
@Getter
@Setter
public class MelhorEnvioProperties {
    private String token;
    private String url;
    private String application;
    private String email;
}
