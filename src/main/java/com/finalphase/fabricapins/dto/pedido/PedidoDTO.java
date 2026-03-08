package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.dto.PedidoCupom.PedidoCupomDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteMinPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoDTO;
import com.finalphase.fabricapins.dto.pagamento.PagamentoDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PedidoDTO(
        Long id,
        Instant dataCriacao,
        Instant dataAtualizacao,
        StatusPedido statusPedido,
        OrigemPedido origemPedido,
        BigDecimal valorTotal,
        BigDecimal valorSubtotal,
        BigDecimal desconto,
        String codigoPedido,
        BigDecimal valorFrete,
        LocalDate dataPrevistaProducao,
        LocalDate dataConclusaoPedido,
        LocalDate dataEnvio,
        LocalDate dataEntrega,
        ClienteMinPedidoDTO cliente,
        EnderecoPedidoDTO enderecoEntrega,
        String observacao,
        PagamentoDTO pagamento,
        List<ItemPedidoDTO> items,
        List<PedidoCupomDTO> cupons
) {}
