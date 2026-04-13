package com.finalphase.fabricapins.ecommerce.dto.pedido;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoPedidoRequest;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.ItemPedidoRequest;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Schema(description = "DTO de requisição do Pedido feito por Admin")
public record PedidoAdminRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Origem do Pedido", example = "SITE")
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

        @Schema(description = "Endereco de entrega", implementation = EnderecoPedidoRequest.class)
        EnderecoPedidoRequest enderecoEntrega,

        @Schema(description = "Observações do pedido")
        String observacao,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Valor do Frete", example = "3.00")
        BigDecimal valorFrete,

        @NotNull(message = "Campo requerido")
        @ArraySchema(schema = @Schema(implementation = ItemPedidoRequest.class),
        arraySchema = @Schema(description = "Itens do pedido")
        )
        List<ItemPedidoRequest> items,

        @ArraySchema(
                arraySchema = @Schema(description = "Cupons de desconto aplicados"),
                schema = @Schema(example = "DESC10")
        )
        Set<String> cupons
) {}
