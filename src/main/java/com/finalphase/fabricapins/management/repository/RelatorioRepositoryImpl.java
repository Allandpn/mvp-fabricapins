package com.finalphase.fabricapins.management.repository;

import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.SituacaoEstoque;
import com.finalphase.fabricapins.ecommerce.domain.enums.StatusPedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.exception.BusinessException;
import com.finalphase.fabricapins.management.dto.*;
import com.finalphase.fabricapins.management.enums.AgrupamentoPeriodo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Repository
public class RelatorioRepositoryImpl implements RelatorioRepositoryCustom {

    private static final Set<String> AGRUPAMENTOS_VALIDOS = Set.of("day", "week", "month", "quarter", "year");

    @PersistenceContext
    private EntityManager em;

    @Override
    public ResumoDTO resumo(Instant dataInicio, Instant dataFim, OrigemPedido canal, TipoCliente tipoCliente, Long categoriaId){
        String sql = """
                SELECT
                    (
                        SELECT count(*)
                        FROM tb_produto pr                
                        LEFT JOIN tb_categoria c ON c.id = pr.categoria_id
                        WHERE pr.quantidade_estoque <= pr.estoque_minimo
                            AND (:categoriaId IS NULL OR c.id = :categoriaId)                        
                    )  as estoqueCritico,
                    (
                        SELECT COALESCE(SUM(p.valor_total_final), 0)
                        FROM tb_pedido p
                        WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_item_pedido ip
                                    JOIN tb_produto pr ON pr.id = ip.produto_id
                                    WHERE ip.pedido_id = p.id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as receitaBruta,
                    (
                        SELECT COALESCE(SUM(p.valor_total_final - p.valor_frete), 0)
                        FROM tb_pedido p
                        WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_item_pedido ip
                                    JOIN tb_produto pr ON pr.id = ip.produto_id
                                    WHERE ip.pedido_id = p.id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as receitaLiquida,
                    (
                        SELECT COALESCE(SUM(ip.custo_unitario_snapshot * ip.quantidade), 0)
                        FROM tb_item_pedido ip
                        LEFT JOIN tb_pedido p ON p.id = ip.pedido_id
                        WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)                        
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_produto pr
                                    WHERE pr.id = ip.produto_id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as custoProducao,
                    (
                        SELECT AVG(EXTRACT(EPOCH FROM (p.data_fim_producao - p.data_inicio_producao)) /3600)
                        FROM tb_pedido p
                        WHERE p.data_inicio_producao IS NOT NULL
                            AND p.data_fim_producao IS NOT NULL
                            AND p.data_inicio_producao BETWEEN :dataInicio AND :dataFim
                            AND (:canal IS NULL OR p.origem_pedido = :canal)
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_item_pedido ip
                                    JOIN tb_produto pr ON pr.id = ip.produto_id
                                    WHERE ip.pedido_id = p.id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as tempoMedioProducao
                """;
        @SuppressWarnings("unchecked")
        Object[] row = (Object[]) em.createNativeQuery(sql)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .setParameter("canal", canal != null ? canal.name() : null)
                .setParameter("tipoCliente", tipoCliente != null ? tipoCliente.name() : null)
                .setParameter("categoriaId", categoriaId)
                .getSingleResult();

        Integer estoqueCritico = row[0] != null ? ((Number) row[0]).intValue() : 0;
        BigDecimal receitaBruta = row[1] != null ? ((BigDecimal) row[1]) : BigDecimal.ZERO;
        BigDecimal receitaLiquida = row[2] != null ? ((BigDecimal) row[2]) : BigDecimal.ZERO;
        BigDecimal custoProducao = row[3] != null ? ((BigDecimal) row[3]) : BigDecimal.ZERO;
        BigDecimal lucroEstimado = receitaLiquida.subtract(custoProducao);
        Double tempoMedioProducao = row[4] != null ? ((Number) row[4]).doubleValue() : 0.0;

        return new ResumoDTO(
                estoqueCritico,
                receitaBruta,
                receitaLiquida,
                lucroEstimado,
                tempoMedioProducao
        );
    }

    @Override
    public List<ProdutoAnalitcsDTO> estoqueProdutos(Instant dataInicio, Instant dataFim, Long categoriaId){

        String sql = """
                SELECT 
                    pr.nome,
                    c.nome as categoriaNome,
                    pr.quantidade_estoque,
                    pr. estoque_minimo,
                    SUM(COALESCE(ip.quantidade, 0)) as vendidoPeriodo,
                    CASE
                        WHEN pr.quantidade_estoque = 0 THEN 'SEM_ESTOQUE'
                        WHEN pr.quantidade_estoque <= pr.estoque_minimo THEN 'ABAIXO_DO_MINIMO'
                        WHEN pr.quantidade_estoque >= pr.estoque_minimo * 1.5 THEN 'ACIMA_DO_MAXIMO'
                        ELSE 'NORMAL'
                    END as situacao
                FROM tb_produto pr
                JOIN tb_categoria c ON c.id = pr.categoria_id
                LEFT JOIN tb_item_pedido ip ON ip.produto_id = pr.id
                LEFT JOIN tb_pedido p ON p.id = ip.pedido_id
                    AND p.status_pedido <> 'CANCELADO'
                    AND p.data_criacao BETWEEN :dataInicio AND :dataFim
                WHERE (:categoriaId IS NULL OR c.id = :categoriaId)
                GROUP BY
                    pr.id, pr.nome, c.nome, pr.quantidade_estoque, pr.estoque_minimo
                ORDER BY pr.nome
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .setParameter("categoriaId", categoriaId)
                .getResultList();

        return rows.stream().map(r -> {
            return new ProdutoAnalitcsDTO(
                    (String) r[0],
                    (String) r[1],
                    ((Number) r[2]).intValue(),
                    ((Number) r[3]).intValue(),
                    ((Number) r[4]).intValue(),
                    SituacaoEstoque.valueOf(r[5].toString())
            );
        }).toList();
    }

    @Override
    public List<PedidoStatusDTO> pedidoStatus(Instant dataInicio, Instant dataFim){
        String sql = """
                SELECT 
                    p.status_pedido as status,
                    COUNT(*) as quantidade
                FROM tb_pedido p
                WHERE p.data_criacao BETWEEN :dataInicio AND :dataFim
                GROUP BY
                    p.status_pedido
                ORDER BY quantidade DESC
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .getResultList();

        return rows.stream().map(r -> {
            return new PedidoStatusDTO(
                    StatusPedido.valueOf(r[0].toString()),
                    ((Number) r[1]).intValue()
            );
        }).toList();
    }

    @Override
    public List<DuracaoProducaoDTO> duracaoProducao(Instant dataInicio, Instant dataFim){
        String sql = """
                SELECT 
                    pr.nome,
                    AVG(EXTRACT(EPOCH FROM (p.data_fim_producao - p.data_inicio_producao)) /3600) as tempoProducao
                FROM tb_pedido p
                LEFT JOIN tb_item_pedido ip ON ip.pedido_id = p.id
                LEFT JOIN tb_produto pr ON pr.id = ip.produto_id
                WHERE p.data_fim_producao IS NOT NULL
                    AND pr.nome IS NOT NULL
                    AND p.data_criacao BETWEEN :dataInicio AND :dataFim
                GROUP BY pr.nome
                ORDER BY tempoProducao DESC
                LIMIT 10
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .getResultList();

        return rows.stream().map(r -> {
            return new DuracaoProducaoDTO(
                    (String) r[0],
                    ((Number) r[1]).doubleValue()
            );
        }).toList();
    }

    @Override
    public ProducaoDTO resumoProducao(Instant dataInicio, Instant dataFim){
        String sql = """
                SELECT
                    (
                        SELECT AVG(EXTRACT(EPOCH FROM (p.data_fim_producao - p.data_inicio_producao)) /3600)
                        FROM tb_pedido p                
                        WHERE p.data_fim_producao IS NOT NULL
                            AND p.data_criacao BETWEEN :dataInicio AND :dataFim                       
                    )  as tempoMedioProducaoHoras,
                    (
                        SELECT SUM(COALESCE(pr.quantidade_estoque, 0))
                        FROM tb_produto pr
                        WHERE pr.tipo_estoque = 'ESTOQUE'
                    )  as quantidadeProntaEntrega,
                    (
                        SELECT SUM(COALESCE(pr.quantidade_estoque, 0))
                        FROM tb_produto pr
                        WHERE pr.tipo_estoque = 'SOB_DEMANDA'
                    )  as quantidadeSobDemanda,
                    (
                        SELECT SUM(COALESCE(pr.quantidade_estoque, 0))
                        FROM tb_produto pr
                        WHERE pr.tipo_estoque = 'PRE_VENDA'
                    )  as quantidadePreVenda
                
                """;
        @SuppressWarnings("unchecked")
        Object[] row = (Object[]) em.createNativeQuery(sql)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .getSingleResult();

        Double tempoMedioProducaoHoras = row[0] != null ? ((Number) row[0]).doubleValue() : 0;
        Integer quantidadeProntaEntrega = row[1] != null ? ((Number) row[1]).intValue() : 0;
        Integer quantidadeSobDemanda = row[2] != null ? ((Number) row[2]).intValue() : 0;
        Integer quantidadePreVenda = row[3] != null ? ((Number) row[3]).intValue() : 0;

        return new ProducaoDTO(
                tempoMedioProducaoHoras,
                quantidadeProntaEntrega,
                quantidadePreVenda,
                quantidadeSobDemanda,
                null,
                null
        );
    }

    @Override
    public ReceitaDTO receita(Instant dataInicio, Instant dataFim, OrigemPedido canal, TipoCliente tipoCliente, Long categoriaId) {
        String sql = """
                SELECT
                    (
                        SELECT COALESCE(SUM(p.valor_total_final), 0)
                        FROM tb_pedido p
                        WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_item_pedido ip
                                    JOIN tb_produto pr ON pr.id = ip.produto_id
                                    WHERE ip.pedido_id = p.id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as receitaBruta,
                    (
                        SELECT COALESCE(SUM(p.valor_total_final - p.valor_frete), 0)
                        FROM tb_pedido p
                        WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_item_pedido ip
                                    JOIN tb_produto pr ON pr.id = ip.produto_id
                                    WHERE ip.pedido_id = p.id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as receitaLiquida,
                    (
                        SELECT COUNT(*)
                        FROM tb_pedido p
                        WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_item_pedido ip
                                    JOIN tb_produto pr ON pr.id = ip.produto_id
                                    WHERE ip.pedido_id = p.id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as quantidadePedidos,
                    (
                        SELECT COALESCE(SUM(ip.quantidade), 0)
                        FROM tb_item_pedido ip
                        LEFT JOIN tb_pedido p ON p.id = ip.pedido_id
                        WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)                        
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_produto pr
                                    WHERE pr.id = ip.produto_id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as totalItens,
                    (
                        SELECT AVG(p.valor_total_final)
                        FROM tb_pedido p
                        WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_item_pedido ip
                                    JOIN tb_produto pr ON pr.id = ip.produto_id
                                    WHERE ip.pedido_id = p.id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                    )  as ticketMedio
                """;
        @SuppressWarnings("unchecked")
        Object[] row = (Object[]) em.createNativeQuery(sql)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .setParameter("canal", canal != null ? canal.name() : null)
                .setParameter("tipoCliente", tipoCliente != null ? tipoCliente.name() : null)
                .setParameter("categoriaId", categoriaId)
                .getSingleResult();

        BigDecimal receitaBruta = row[0] != null ? ((BigDecimal) row[0]) : BigDecimal.ZERO;
        BigDecimal receitaLiquida = row[1] != null ? ((BigDecimal) row[1]) : BigDecimal.ZERO;
        Integer quantidadePedidos = row[2] != null ? ((Number) row[2]).intValue() : 0;
        Integer totalItens = row[3] != null ? ((Number) row[3]).intValue() : 0;
        BigDecimal ticketMedio = row[4] != null ? ((BigDecimal) row[4]).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        return new ReceitaDTO(
                receitaBruta,
                receitaLiquida,
                quantidadePedidos,
                totalItens,
                ticketMedio
        );
    }

    @Override
    public List<VendasCanalDTO> vendasPorCanal(Instant dataInicio, Instant dataFim, TipoCliente tipoCliente, Long categoriaId){
        String sql = """
                SELECT 
                    p.origem_pedido,
                    COUNT(*) as quantidadePedidos,
                    SUM(p.valor_total_final) as receita
                FROM tb_pedido p
                WHERE p.status_pedido <> 'CANCELADO'
                    AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                    AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente) 
                    AND (
                        :categoriaId IS NULL
                        OR EXISTS (
                            SELECT 1
                            FROM tb_item_pedido ip
                            JOIN tb_produto pr ON pr.id = ip.produto_id
                            WHERE ip.pedido_id = p.id
                              AND pr.categoria_id = :categoriaId
                        )
                    )
                GROUP BY p.origem_pedido
                ORDER BY receita DESC
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .setParameter("tipoCliente", tipoCliente != null ? tipoCliente.name() : null)
                .setParameter("categoriaId", categoriaId)
                .getResultList();

        BigDecimal receitaTotal =  rows.stream().map(r -> r[2] != null ? ((BigDecimal) r[2]) : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);

        return rows.stream().map(r -> {
            Integer quantidadePedidos = r[1] != null ? ((Number) r[1]).intValue() : 0;
            BigDecimal receita = r[2] != null ? ((BigDecimal) r[2]) : BigDecimal.ZERO;
            // TODO
            BigDecimal percentualParticipacao =
                    receitaTotal.compareTo(BigDecimal.ZERO) > 0
                            ? receita.divide(receitaTotal,6, RoundingMode.HALF_UP)
                              .multiply(BigDecimal.valueOf(100))
                              .setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
            return new VendasCanalDTO(
                    OrigemPedido.valueOf(r[0].toString()),
                    quantidadePedidos,
                    percentualParticipacao,
                    receita
            );
        }).toList();
    }


    @Override
    public List<VendasPeriodoDTO> historicoVendas(Instant dataInicio, Instant dataFim, String periodo, OrigemPedido canal, TipoCliente tipoCliente, Long categoriaId){
        String sql = """
                SELECT
                    DATE_TRUNC('%s', p.data_pagamento_confirmado) as periodo,                    
                    COUNT(DISTINCT p.id) as quantidadePedidos,
                    COALESCE(SUM((
                        SELECT SUM(ip.quantidade)
                        FROM tb_item_pedido ip
                        WHERE ip.pedido_id = p.id
                        )), 0) as quantidadeItens,
                    COALESCE(SUM(p.valor_total_final), 0) as receita
                FROM tb_pedido p
                WHERE p.status_pedido <> 'CANCELADO'
                            AND p.data_pagamento_confirmado BETWEEN :dataInicio AND :dataFim
                            AND (:tipoCliente IS NULL OR p.tipo_cliente = :tipoCliente)                       
                            AND (:canal IS NULL OR p.origem_pedido = :canal)
                            AND (
                                :categoriaId IS NULL
                                OR EXISTS (
                                    SELECT 1
                                    FROM tb_item_pedido ip
                                    JOIN tb_produto pr ON pr.id = ip.produto_id
                                    WHERE ip.pedido_id = p.id
                                      AND pr.categoria_id = :categoriaId
                                )
                            )
                GROUP BY periodo
                ORDER BY periodo
                """.formatted(periodo);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("dataInicio", dataInicio)
                .setParameter("dataFim", dataFim)
                .setParameter("canal", canal != null ? canal.name() : null)
                .setParameter("tipoCliente", tipoCliente != null ? tipoCliente.name() : null)
                .setParameter("categoriaId", categoriaId)
                .getResultList();

        return rows.stream().map(r -> {
            Instant periodoDTO = r[0] instanceof OffsetDateTime odt
                    ? odt.toInstant()
                    : ((java.sql.Timestamp) r[0]).toInstant();
            Integer quantidadePedidos = r[1] != null ? ((Number) r[1]).intValue() : 0;
            Integer quantidadeItens = r[2] != null ? ((Number) r[2]).intValue() : 0;
            BigDecimal receita = r[3] != null ? ((BigDecimal) r[3]) : BigDecimal.ZERO;
            return new VendasPeriodoDTO(periodoDTO, null, quantidadePedidos, quantidadeItens, receita, null);
        }).toList();
    }



    }

























//
//
//
//
//    @Override
//    public List<ReceitaDTO> receitaAgrupada(Instant inicio, Instant fim, String periodo, String canal) {
//        if (!AGRUPAMENTOS_VALIDOS.contains(periodo)) {
//            throw new BusinessException("Agrupamento inválido: " + periodo);
//        }
//
//        String sql = """
//                SELECT
//                    DATE_TRUNC('%s', p.data_pagamento_confirmado) as periodo,
//                    SUM(p.valor_total_final) as total
//                FROM tb_pedido p
//                WHERE p.status_pedido <> 'CANCELADO'
//                    AND p.data_pagamento_confirmado BETWEEN :inicio AND :fim
//                    AND (:canal IS NULL OR p.origem_pedido = :canal)
//                GROUP BY periodo
//                ORDER BY periodo
//                """.formatted(periodo);
//
//        @SuppressWarnings("unchecked")
//        List<Object[]> rows = em.createNativeQuery(sql)
//                .setParameter("inicio", inicio)
//                .setParameter("fim", fim)
//                .setParameter("canal", canal)
//                .getResultList();
//
//        return rows.stream().map(r -> {
//            Instant periodoDTO = r[0] instanceof OffsetDateTime odt
//                    ? odt.toInstant()
//                    : ((java.sql.Timestamp) r[0]).toInstant();
//            BigDecimal total = r[1] instanceof BigDecimal bd ? bd : BigDecimal.valueOf(((Number) r[1]).doubleValue());
//            return new ReceitaDTO(periodoDTO, null, total);
//        }).toList();
//    }
//
//    @Override
//    public List<ProducaoDTO> producaoAgrupada(Instant inicio, Instant fim, String canal, String dimensao, Long produtoId, Long variacaoId, Long categoriaId) {
////
////
//        String sql = """
//                SELECT
//                    CASE
//                        WHEN :dimensao = 'PRODUTO' THEN COALESCE(pr.nome, 'SEM_PRODUTO')
//                        WHEN :dimensao = 'VARIACAO' THEN COALESCE(pv.nome, 'SEM_VARIACAO')
//                        WHEN :dimensao = 'CATEGORIA' THEN COALESCE(c.nome, 'SEM_CATEGORIA')
//                        ELSE 'GERAL'
//                    END as grupo,
//                    COUNT(DISTINCT p.id) as quantidade,
//                    AVG(EXTRACT(EPOCH FROM (p.data_fim_producao - p.data_inicio_producao)) /3600) as tempo_medio
//                    FROM tb_pedido p
//                    LEFT JOIN tb_item_pedido ip ON ip.pedido_id = p.id
//                    LEFT JOIN tb_produto_variacao pv ON pv.id = ip.produto_variacao_id
//                    LEFT JOIN tb_produto pr ON pr.id = pv.produto_id
//                    LEFT JOIN tb_categoria c ON c.id = pr.categoria_id
//                    WHERE p.data_inicio_producao IS NOT NULL
//                        AND p.data_fim_producao IS NOT NULL
//                        AND p.data_inicio_producao BETWEEN :inicio AND :fim
//                        AND (:canal IS NULL OR p.origem_pedido = :canal)
//                        AND (:produtoId IS NULL OR pr.id = :produtoId)
//                        AND (:variacaoId IS NULL OR pv.id = :variacaoId)
//                        AND (:categoriaId IS NULL OR c.id = :categoriaId)
//                    GROUP BY grupo
//                    ORDER BY tempo_medio DESC
//                """;
//
//        @SuppressWarnings("unchecked")
//        List<Object[]> rows = em.createNativeQuery(sql)
//                .setParameter("inicio", inicio)
//                .setParameter("fim", fim)
//                .setParameter("canal", canal)
//                .setParameter("produtoId", produtoId)
//                .setParameter("variacaoId", variacaoId)
//                .setParameter("categoriaId", categoriaId)
//                .setParameter("dimensao", dimensao)
//                .getResultList();
//
//        return rows.stream().map(r -> {
//            String grupo = (String) r[0];
//            Long quantidade = r[1] != null ? ((Number) r[1]).longValue() : 0L;
//            Double tempoMedio = r[2] != null
//                    ? ((Number) r[2]).doubleValue()
//                    : 0;
//
//            return new ProducaoDTO(
//                    grupo,
//                    tempoMedio,
//                    quantidade            );
//        }).toList();
//        return null;
//    }
//
//
//    @Override
//    public List<Object[]> volumeAgrupado(Instant inicio,Instant fim,String canal,String periodo,String dimensao,Long produtoId,Long variacaoId,Long categoriaId) {
//
//        String sql = """
//        SELECT
//            DATE_TRUNC('%s', p.data_pagamento_confirmado) as periodo,
//            CASE
//                WHEN :dimensao = 'PRODUTO' THEN COALESCE(pr.nome, 'SEM_PRODUTO')
//                WHEN :dimensao = 'VARIACAO' THEN COALESCE(pv.nome, 'SEM_VARIACAO')
//                WHEN :dimensao = 'CATEGORIA' THEN COALESCE(c.nome, 'SEM_CATEGORIA')
//                ELSE 'GERAL'
//            END as grupo,
//            COUNT(DISTINCT p.id) as quantidade_pedidos,
//            COALESCE(SUM(ip.quantidade), 0) as quantidade_itens,
//            COALESCE(SUM(p.valor_total_final), 0) as receita
//        FROM tb_pedido p
//        LEFT JOIN tb_item_pedido ip ON ip.pedido_id = p.id
//        LEFT JOIN tb_produto_variacao pv ON pv.id = ip.produto_variacao_id
//        LEFT JOIN tb_produto pr ON pr.id = pv.produto_id
//        LEFT JOIN tb_categoria c ON c.id = pr.categoria_id
//        WHERE p.status_pedido <> 'CANCELADO'
//            AND p.data_pagamento_confirmado BETWEEN :inicio AND :fim
//            AND (:canal IS NULL OR p.origem_pedido = :canal)
//            AND (:produtoId IS NULL OR pr.id = :produtoId)
//            AND (:variacaoId IS NULL OR pv.id = :variacaoId)
//            AND (:categoriaId IS NULL OR c.id = :categoriaId)
//
//        GROUP BY periodo, grupo
//        ORDER BY receita DESC, periodo DESC
//    """.formatted(periodo);
//
//        return em.createNativeQuery(sql)
//                .setParameter("inicio", inicio)
//                .setParameter("fim", fim)
//                .setParameter("canal", canal)
//                .setParameter("dimensao", dimensao)
//                .setParameter("produtoId", produtoId)
//                .setParameter("variacaoId", variacaoId)
//                .setParameter("categoriaId", categoriaId)
//                .getResultList();
//    }
//
//
//    @Override
//    public List<Object[]> estoqueAnalitico(String dimensao,Long produtoId,Long variacaoId,Long categoriaId,Instant demandaInicio,Instant demandaFim) {
//
//        String sql = """
//        SELECT
//            CASE
//                WHEN :dimensao = 'PRODUTO' THEN COALESCE(pr.nome, 'SEM_PRODUTO')
//                WHEN :dimensao = 'VARIACAO' THEN COALESCE(pv.nome, 'SEM_VARIACAO')
//                WHEN :dimensao = 'CATEGORIA' THEN COALESCE(c.nome, 'SEM_CATEGORIA')
//                ELSE 'GERAL'
//            END as grupo,
//            SUM(pv.quantidade_estoque) as quantidade,
//            SUM(pv.estoque_minimo) as estoque_minimo,
//            COALESCE(SUM(ip.quantidade) FILTER (
//                WHERE p.data_pagamento_confirmado IS NOT NULL
//                AND p.status_pedido <> 'CANCELADO'
//                AND p.data_pagamento_confirmado BETWEEN :demandaInicio AND :demandaFim
//            ), 0) as demanda_recente
//        FROM tb_produto_variacao pv
//        LEFT JOIN tb_item_pedido ip ON ip.produto_variacao_id = pv.id
//        LEFT JOIN tb_pedido p ON p.id = ip.pedido_id
//        LEFT JOIN tb_produto pr ON pr.id = pv.produto_id
//        LEFT JOIN tb_categoria c ON c.id = pr.categoria_id
//        WHERE pv.ativo = true
//            AND (:produtoId IS NULL OR pr.id = :produtoId)
//            AND (:variacaoId IS NULL OR pv.id = :variacaoId)
//            AND (:categoriaId IS NULL OR c.id = :categoriaId)
//        GROUP BY grupo
//        ORDER BY quantidade ASC
//    """;
//
//        return em.createNativeQuery(sql)
//                .setParameter("dimensao", dimensao)
//                .setParameter("produtoId", produtoId)
//                .setParameter("variacaoId", variacaoId)
//                .setParameter("categoriaId", categoriaId)
//                .setParameter("demandaInicio", demandaInicio)
//                .setParameter("demandaFim", demandaFim)
//                .getResultList();
//    }
//}
