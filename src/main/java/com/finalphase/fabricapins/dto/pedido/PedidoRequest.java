package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.entities.ItemPedido;
import com.finalphase.fabricapins.domain.entities.PedidoCupom;
import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.dto.cliente.ClienteMinPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.dto.pagamento.PagamentoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Schema(description = "DTO de requisição do Pedido")
public record PedidoRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Origem do Pedido", example = "SITE")
        OrigemPedido origemPedido,

        @Schema(description = "Id do Cliente", example = "1")
        Long clienteId,

        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 150, message = "Nome do Cliente precisa estar entre 3 e 150 caracteres")
        @Schema(description = "Nome do Cliente", example = "Maria da Silva")
        String nomeClienteSnapshot,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Numero do Documento", example = "00055522266")
        String documentoClienteSnapshot,

        @NotNull(message = "Campo requerido")
        @Schema(description = "Endereco de entrega", example = "{'cep': '12345678','estado': 'SP','cidade': 'Sao Paulo','bairro': 'Centro','logradouro': 'Rua Sao paulo','numero': '100','complemento': 'casa 03','pontoReferencia': 'proximo Praça da Sé'}")
        EnderecoPedidoDTO enderecoDTO,

        @NotNull(message = "Campo requerido")
        @Schema(description = "Itens do Pedido", example = "{'produtoVariacaoId': 12,'quantidade': 2}")
        List<ItemPedidoRequest> items,

        @Schema(description = "Cupons de Desconto", example = "['DESC10', 'FIXO15']")
        Set<String> cupons
) {}
