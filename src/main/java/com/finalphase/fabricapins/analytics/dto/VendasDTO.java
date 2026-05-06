package com.finalphase.fabricapins.analytics.dto;

import java.time.Instant;

public record VendasDTO(
        Instant periodo,
        String label,
        String grupo,
        Long quantidadePedidos,
        Long quantidadeItens,
        Double receita
) {}
