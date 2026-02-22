package com.finalphase.fabricapins.dto.cupom_desconto;

import com.finalphase.fabricapins.domain.enums.TipoDesconto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CupomDescontoRequest(
        String codigo,
        boolean ativo,
        BigDecimal valorDesconto,
        TipoDesconto tipoDesconto,
        LocalDate dataValidade,
        Integer quantidadeMinimaItens,
        BigDecimal valorMinimoPedido,
        Integer limiteUsos
) {
}
