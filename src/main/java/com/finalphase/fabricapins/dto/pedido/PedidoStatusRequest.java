package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Schema(description = "DTO de requisição do Pedido feito por Admin")
public record PedidoStatusRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Novo status do pedido", example = "EM_PRODUCAO")
        StatusPedido statusPedido
) {}
