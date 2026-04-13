package com.finalphase.fabricapins.ecommerce.dto.item_pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO de requisição para adicionar Item ao Pedido feito por Admin")
public record ItemPedidoRequest (
        @NotNull(message = "Campo requerido")
        @Schema(description = "Id do item", example = "1")
        Long id,
        @NotNull(message = "Campo requerido")
        @Schema(description = "Quantidade do Item", example = "2")
        Integer quantidade
){}
