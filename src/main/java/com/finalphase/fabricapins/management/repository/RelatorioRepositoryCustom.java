package com.finalphase.fabricapins.management.repository;

import com.finalphase.fabricapins.management.dto.ProducaoDTO;
import com.finalphase.fabricapins.management.dto.ReceitaDTO;

import java.time.Instant;
import java.util.List;

public interface RelatorioRepositoryCustom {
    List<ReceitaDTO> receitaAgrupada(Instant inicio, Instant fim, String agrupamento, String canal);

    List<ProducaoDTO> producaoAgrupada(Instant inicio, Instant fim, String canal, String agrupamento, Long produtoId, Long variacaoId, Long categoriaId);
}
