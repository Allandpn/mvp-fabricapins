package com.finalphase.fabricapins.management.dto;

import java.time.Instant;

public record VendasPeriodoDTO(
        Instant instant,
        String label,
        Integer quantidadePedidos,
        Integer quantidadeItens,
        Double receita,
        Double crescimentoPercentual
) {}
