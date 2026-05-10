package com.finalphase.fabricapins.management.repository;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.management.dto.*;
import com.finalphase.fabricapins.management.enums.AgrupamentoPeriodo;

import java.time.Instant;
import java.util.List;

public interface RelatorioRepositoryCustom {
//    List<ReceitaDTO> receitaAgrupada(Instant inicio, Instant fim, String agrupamento, String canal);
//
//    List<ProducaoDTO> producaoAgrupada(Instant inicio, Instant fim, String canal, String agrupamento, Long produtoId, Long variacaoId, Long categoriaId);
//
//    List<Object[]> volumeAgrupado(Instant inicio, Instant fim, String canal, String periodo, String dimensao, Long produtoId, Long variacaoId, Long categoriaId);
//
//    List<Object[]> estoqueAnalitico(String dimensao,Long produtoId,Long variacaoId,Long categoriaId,Instant demandaInicio,Instant demandaFim);

    ResumoDTO resumo(Instant dataInicio, Instant dataFim, OrigemPedido canal, TipoCliente tipoCliente, Long categoriaId);

    List<ProdutoAnalitcsDTO> estoqueProdutos(Instant dataInicio, Instant dataFim, Long categoriaId);

    List<PedidoStatusDTO> pedidoStatus(Instant dataInicio, Instant dataFim);

    List<DuracaoProducaoDTO> duracaoProducao(Instant dataInicio, Instant dataFim);

    ProducaoDTO resumoProducao(Instant dataInicio, Instant dataFim);

    ReceitaDTO receita(Instant dataInicio, Instant dataFim, OrigemPedido canal, TipoCliente tipoCliente, Long categoriaId);

    List<VendasCanalDTO> vendasPorCanal(Instant dataInicio, Instant dataFim, TipoCliente tipoCliente, Long categoriaId);

    List<VendasPeriodoDTO> historicoVendas(Instant dataInicio, Instant dataFim, String periodo, OrigemPedido canal, TipoCliente tipoCliente, Long categoriaId);
}
