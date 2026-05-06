package com.finalphase.fabricapins.analytics.dto;

public record EstoqueDTO(
        String grupo,
        Integer quantidade,
        Integer estoqueMinimo,
        String status,
        Long demandaRecente
) {}
