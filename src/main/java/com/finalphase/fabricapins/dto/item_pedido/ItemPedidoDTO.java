package com.finalphase.fabricapins.dto.item_pedido;

import java.math.BigDecimal;

public record ItemPedidoDTO(
        Long produtoVariacaoId,
        String produtoVariacaoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subTotal
){}
