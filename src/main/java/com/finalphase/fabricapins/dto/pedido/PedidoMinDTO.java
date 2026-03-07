package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;

public record PedidoMinDTO (
        Long id,
        Instant dataCriacao,
        StatusPedido statusPedido,
        BigDecimal valorTotal,
        String numeroPedido
){}
