package com.finalphase.fabricapins.dto.PedidoCupom;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PedidoCupomDTO(
        String codigo,
        BigDecimal valorDescontoAplicado,
        Instant dataAplicacao
) {
}
