package com.finalphase.fabricapins.ecommerce.dto.frete;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;


public record OpcaoFreteDTO(
        String serviceId,
        String nome,
        BigDecimal valor,
        Integer prazoDias,
        String empresa
) {}
