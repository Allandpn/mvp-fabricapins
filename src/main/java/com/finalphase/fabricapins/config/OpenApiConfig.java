package com.finalphase.fabricapins.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi ecommerceApi() {
        return GroupedOpenApi.builder()
                .group("Ecommerce")
                .packagesToScan("com.finalphase.fabricapins.ecommerce")
                .build();
    }

    @Bean
    public GroupedOpenApi gestaoApi() {
        return GroupedOpenApi.builder()
                .group("Gestao")
                .packagesToScan("com.finalphase.fabricapins.management")
                .build();
    }

    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("Autenticação")
                .packagesToScan("com.finalphase.fabricapins.config.security.auth")
                .build();
    }
}
