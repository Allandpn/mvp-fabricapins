package com.finalphase.fabricapins.ecommerce.dto.pedido;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.StatusPedido;
import com.finalphase.fabricapins.ecommerce.dto.PedidoCupom.PedidoCupomDTO;
import com.finalphase.fabricapins.ecommerce.dto.cliente.ClienteMinPedidoDTO;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.ItemPedidoDTO;
import com.finalphase.fabricapins.ecommerce.dto.pagamento.PagamentoDTO;

import java.math.BigDecimal;
import java.time.Instant;
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
        String freteServiceId,
        BigDecimal valorFrete,
        String nomeServicoFrete,
        Integer prazoEntregaDias,
        Instant dataInicioProducao,
        Instant dataConclusaoPedido,
        Instant dataEnvio,
        Instant dataEntrega,
        ClienteMinPedidoDTO cliente,
        EnderecoPedidoDTO enderecoEntrega,
        String observacao,
        PagamentoDTO pagamento,
        List<ItemPedidoDTO> items,
        List<PedidoCupomDTO> cupons
) {}
