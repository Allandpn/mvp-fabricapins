package com.finalphase.fabricapins.integration.frete.dto;


public record MelhorEnvioResponse(
        Integer id,
        String name,
        String custom_price,
        String discount,
        String currency,
        Integer custom_delivery_time,
        String error,
        MelhorEnvioCompany company
) {}
