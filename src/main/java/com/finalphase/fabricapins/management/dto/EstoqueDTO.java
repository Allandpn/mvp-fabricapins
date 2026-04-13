package com.finalphase.fabricapins.management.dto;

public record EstoqueDTO(
        String grupo,
        Integer quantidade,
        Integer estoqueMinimo,
        String status,
        Long demandaRecente
) {}
