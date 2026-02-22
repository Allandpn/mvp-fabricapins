package com.finalphase.fabricapins.dto.pagamento;

import com.finalphase.fabricapins.domain.enums.FormaPagamento;
import com.finalphase.fabricapins.domain.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.Instant;

public record PagamentoDTO(
        Long id,
        Instant dataPagamento,
        BigDecimal valorPago,
        FormaPagamento formaPagamento,
        StatusPagamento statusPagamento,
        String codigoTransacao,
        String gatewayPagamento,
        Integer parcelasCartao,
        Instant dataConfirmacao,
        String motivoRecusa,
        Long pedidoId
) {}

