package com.finalphase.fabricapins.dto.PedidoCupom;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoCupomDTO(
        LocalDate dataAplicacao,
        BigDecimal valorDescontoAplicado
) {
}
