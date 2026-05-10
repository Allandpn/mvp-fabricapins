package com.finalphase.fabricapins.management.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReceitaDTO(
        BigDecimal receitaBruta,
        BigDecimal receitaLiquida,
        Integer quantidadePedidos,
        Integer quantidadePedidosCancelados,
        Integer totalItens,
        BigDecimal ticketMedio
) {}
