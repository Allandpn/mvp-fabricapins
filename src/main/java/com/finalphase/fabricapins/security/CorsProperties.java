package com.finalphase.fabricapins.security;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {
    private List<String> allowedOrigins;

    public List<String> getAllowrdOrigins() {
        return allowedOrigins;
    }

    public void setAllowrdOrigins(List<String> allowrdOrigins){
        this.allowedOrigins = allowrdOrigins;
    }
}
