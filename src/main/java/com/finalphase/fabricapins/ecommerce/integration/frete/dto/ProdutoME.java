package com.finalphase.fabricapins.ecommerce.integration.frete.dto;

public record ProdutoME(
        String id,
        Integer width,
        Integer height,
        Integer length,
        Double weight,
        Double insurance_value,
        Integer quantity
) {}
