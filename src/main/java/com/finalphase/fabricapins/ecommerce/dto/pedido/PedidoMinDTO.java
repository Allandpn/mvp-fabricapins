package com.finalphase.fabricapins.ecommerce.dto.pedido;

import com.finalphase.fabricapins.ecommerce.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;

public record PedidoMinDTO(
        Long id,
        Instant dataCriacao,
        StatusPedido statusPedido,
        BigDecimal valorTotal,
        String codigoPedido
){}
