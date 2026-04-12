package com.finalphase.fabricapins.ecommerce.dto.pagamento;

import com.finalphase.fabricapins.ecommerce.domain.enums.FormaPagamento;

import java.math.BigDecimal;

public record PagamentoRequest(
        BigDecimal valorPago,
        FormaPagamento formaPagamento,
        String codigoTransacao,
        String gatewayPagamento,
        Integer parcelasCartao,
        Long pedidoId
) {}
