package com.finalphase.fabricapins.management.dto;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.management.enums.AgrupamentoPeriodo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "DTO de requisição do relatório de Receitas")
public record ReceitaRequest(

        @NotNull(message = "Campo requerido")
        @Schema(description = "Inicio do Periodo", example = "2025-01-01T00:00:00Z")
        Instant dataInicio,

        @NotNull(message = "Campo requerido")
        @Schema(description = "Fim do Periodo", example = "2026-01-01T00:00:00Z")
        Instant dataFim,

        @NotNull(message = "Campo requerido")
        @Schema(description = "Agrupamento do Periodo", example = "MES")
        AgrupamentoPeriodo periodo,

        @Schema(description = "Origem dos Pedidos", example = "SITE")
        OrigemPedido canal
) {}
