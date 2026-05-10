package com.finalphase.fabricapins.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VendasPeriodoDTO {
    private Instant periodo;
    private String label;
    private Integer quantidadePedidos;
    private Integer quantidadeItens;
    private BigDecimal receita;
    private Double crescimentoPercentual;
}
