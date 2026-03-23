package com.finalphase.fabricapins.dto.item_pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO de requisição para alterar Item ao Pedido")
public record QuantidadeItemRequest (
        @NotNull(message = "Campo requerido")
        @Schema(description = "Quantidade do Item", example = "2")
        Integer quantidade
){}