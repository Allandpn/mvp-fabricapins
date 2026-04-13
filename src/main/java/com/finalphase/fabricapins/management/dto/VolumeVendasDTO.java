package com.finalphase.fabricapins.management.dto;

import java.time.Instant;

public record VolumeVendasDTO(
        Instant periodo,
        String label,
        String grupo,
        Long quantidadePedidos,
        Long quantidadeItens,
        Double receita
) {}
