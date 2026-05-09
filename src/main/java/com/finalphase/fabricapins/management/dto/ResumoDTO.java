package com.finalphase.fabricapins.management.dto;

import java.math.BigDecimal;

public record ResumoDTO(
        Integer estoqueCritico,
        BigDecimal receitaBruta,
        BigDecimal receitaLiquida,
        BigDecimal lucroEstimado,
        Double tempoMedioProducao
) {}
