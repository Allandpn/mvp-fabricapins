package com.finalphase.fabricapins.ecommerce.dto.pedido;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;

public record PedidoAdminDTO (
    Long id,
    String nomeCliente,
    BigDecimal valorTotal,
    StatusPedido statusPedido,
    Instant dataCriacao,
    OrigemPedido origemPedido
) {}
