package com.finalphase.fabricapins.management.projection;

import java.math.BigDecimal;
import java.time.Instant;

public interface ReceitaProjection {
    Instant getPeriodo();
    BigDecimal getTotal();
}
