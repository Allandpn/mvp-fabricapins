package com.finalphase.fabricapins.management.dto;

import java.math.BigDecimal;
import java.util.List;

public record PlanejamentoDTO(
        BigDecimal receitaTotal,
        List<VendasCanalDTO> vendasPorCanal,
        List<VendasPeriodoDTO> historicoVendas
) {}
