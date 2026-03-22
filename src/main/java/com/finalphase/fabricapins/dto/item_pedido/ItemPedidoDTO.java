package com.finalphase.fabricapins.dto.item_pedido;

import java.math.BigDecimal;


public record ItemPedidoDTO(
        Long id,
        String nome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subTotal
){}
