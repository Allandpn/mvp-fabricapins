package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.enums.StatusPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO de requisição do Pedido feito por Admin")
public record PedidoStatusRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Novo status do pedido", example = "EM_PRODUCAO")
        StatusPedido statusPedido
) {}
