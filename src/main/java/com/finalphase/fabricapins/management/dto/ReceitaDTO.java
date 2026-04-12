package com.finalphase.fabricapins.management.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ReceitaDTO(
        Instant periodo,
        String label,
        BigDecimal total
) {}
