package com.finalphase.fabricapins.ecommerce.dto.cupom_desconto;

import java.math.BigDecimal;

public record CupomMinPedidoDTO(
        Long id,
        String codigo,
        BigDecimal valorDesconto
) {}
