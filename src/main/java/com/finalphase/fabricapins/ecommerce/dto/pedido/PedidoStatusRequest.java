package com.finalphase.fabricapins.ecommerce.dto.pedido;

import com.finalphase.fabricapins.ecommerce.domain.enums.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO de requisição do Pedido feito por Admin")
public record PedidoStatusRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Novo status do pedido", example = "EM_PRODUCAO")
        StatusPedido statusPedido
) {}
