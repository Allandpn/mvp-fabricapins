package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.TipoCliente;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de requisição do Pedido feito por Admin - Etapa Inicial")
public record PedidoRascunhoRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Origem do Pedido", example = "WHATSAPP")
        OrigemPedido origemPedido,

        @Schema(description = "Id do Cliente", example = "1")
        Long clienteId,

        @Size(min = 3, max = 150, message = "Nome do Cliente precisa estar entre 3 e 150 caracteres")
        @Schema(description = "Nome do Cliente", example = "Maria da Silva")
        String nomeCliente,

        @Schema(description = "Numero do Documento", example = "00055522266")
        String documentoCliente,

        @Schema(description = "Telefone do Cliente", example = "49999999999")
        String telefone,

        @Schema(description = "Tipo de Cliente", example = "VAREJO")
        TipoCliente tipoCliente,

        @Schema(description = "Observações do pedido")
        String observacao
) {}
