package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.entities.Cliente;
import com.finalphase.fabricapins.domain.entities.ItemPedido;
import com.finalphase.fabricapins.domain.entities.Pagamento;
import com.finalphase.fabricapins.domain.entities.PedidoCupom;
import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.dto.PedidoCupom.PedidoCupomDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteMinPedidoDTO;
import com.finalphase.fabricapins.dto.cupom_desconto.CupomMinPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoDTO;
import com.finalphase.fabricapins.dto.pagamento.PagamentoDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record PedidoDTO(
        Long id,
        Instant dataCriacao,
        Instant dataAtualizacao,
        StatusPedido statusPedido,
        OrigemPedido origemPedido,
        BigDecimal valorTotal,
        BigDecimal valorSubtotal,
        BigDecimal desconto,
        String numeroPedido,
        BigDecimal valorFrete,
        LocalDate dataPrevistaProducao,
        LocalDate dataConclusaoPedido,
        LocalDate dataEnvio,
        LocalDate dataEntrega,
        ClienteMinPedidoDTO cliente,
        EnderecoPedidoDTO enderecoDTO,
        PagamentoDTO pagamento,
        List<ItemPedidoDTO> itemsPedido,
        Set<PedidoCupomDTO> pedidoCupomSet
) {}
