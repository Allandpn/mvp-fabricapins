package com.finalphase.fabricapins.management.dto;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.management.enums.AgrupamentoDimensao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "DTO de requisição do relatório de Producao")
public record ProducaoRequest(

        @NotNull(message = "Campo requerido")
        @Schema(description = "Inicio do Periodo", example = "2025-01-01T00:00:00Z")
        Instant dataInicio,

        @NotNull(message = "Campo requerido")
        @Schema(description = "Inicio do Periodo", example = "2025-01-01T00:00:00Z")
        Instant dataFim,

        @Schema(description = "Agrupamento de retorno", example = "CATEGORIA")
        AgrupamentoDimensao agrupamento,

        @Schema(description = "Origem dos Pedidos", example = "SITE")
        OrigemPedido canal,

        @Schema(description = "Id do Produto", example = "1")
        Long produtoId,

        @Schema(description = "Id da Variacao do Produto", example = "1")
        Long produtoVariacaoId,

        @Schema(description = "Id da Categoria", example = "1")
        Long categoriaId
) {}
