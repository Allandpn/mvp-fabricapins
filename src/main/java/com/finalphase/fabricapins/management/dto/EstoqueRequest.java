package com.finalphase.fabricapins.management.dto;

import com.finalphase.fabricapins.management.enums.AgrupamentoDimensao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "DTO de requisição do relatório de Estoque")
public record EstoqueRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Agrupamento da consulta", example = "PRODUTO")
        AgrupamentoDimensao dimensao,

        @Schema(description = "Id do Produto", example = "1")
        Long produtoId,

        @Schema(description = "Id da Variacao do Produto", example = "1")
        Long produtoVariacaoId,

        @Schema(description = "Id da Categoria", example = "1")
        Long categoriaId,

        @Schema(description = "Inicio do Periodo", example = "2025-01-01T00:00:00Z")
        Instant demandaInicio,

        @Schema(description = "Fim do Periodo", example = "2025-01-01T00:00:00Z")
        Instant demandaFim
) {}
