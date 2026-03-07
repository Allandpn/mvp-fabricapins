package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.entities.ItemPedido;
import com.finalphase.fabricapins.domain.entities.PedidoCupom;
import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.dto.cliente.ClienteMinPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.dto.pagamento.PagamentoDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record PedidoRequest(
        OrigemPedido origemPedido,
        Long clienteId,
        String nomeClienteSnapshot,
        String cpfCnpjClienteSnapshot,
        EnderecoPedidoDTO enderecoDTO,
        List<ItemPedidoRequest> items,
        Set<String> cupons
) {}
