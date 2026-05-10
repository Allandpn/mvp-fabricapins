package com.finalphase.fabricapins.management.dto;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;

import java.math.BigDecimal;
import java.time.Instant;

public record VendasCanalDTO(
        OrigemPedido canal,
        Integer quantidadePedidos,
        BigDecimal percentualParticipacao,
        BigDecimal receita
) {}
