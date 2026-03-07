package com.finalphase.fabricapins.dto.pagamento;

import com.finalphase.fabricapins.domain.enums.FormaPagamento;
import com.finalphase.fabricapins.domain.enums.StatusPagamento;

import java.math.BigDecimal;

public record PagamentoRequest(
        BigDecimal valorPago,
        FormaPagamento formaPagamento,
        String codigoTransacao,
        String gatewayPagamento,
        Integer parcelasCartao,
        Long pedidoId
) {}
