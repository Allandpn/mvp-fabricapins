package com.finalphase.fabricapins.ecommerce.dto.item_pedido;

import java.math.BigDecimal;


public record ItemPedidoDTO(
        Long id,
        String nome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subTotal
){}
