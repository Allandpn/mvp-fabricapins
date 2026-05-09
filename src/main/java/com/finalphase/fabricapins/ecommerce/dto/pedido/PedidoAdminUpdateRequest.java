package com.finalphase.fabricapins.ecommerce.dto.pedido;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.StatusPedido;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.ItemPedidoRequest;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Schema(description = "DTO de requisição do Pedido feito por Admin")
public record PedidoAdminUpdateRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Origem do Pedido", example = "SITE")
        OrigemPedido origemPedido,

        @NotNull
        @Schema(description = "Id do Cliente", example = "1")
        Long clienteId,

        @Schema(description = "Observações do pedido")
        String observacao,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Valor do Frete", example = "3.00")
        BigDecimal valorFrete,

        @NotNull
        StatusPedido status
) {}
