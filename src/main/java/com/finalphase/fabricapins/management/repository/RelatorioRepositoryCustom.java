package com.finalphase.fabricapins.management.repository;

import com.finalphase.fabricapins.management.projection.ReceitaProjection;

import java.time.Instant;
import java.util.List;

public interface RelatorioRepositoryCustom {
    List<ReceitaProjection> receitaAgrupada(Instant inicio, Instant fim, String agrupamento, String canal);
}
