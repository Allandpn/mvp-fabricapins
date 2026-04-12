package com.finalphase.fabricapins.management.repository;

import com.finalphase.fabricapins.management.dto.ProducaoDTO;
import com.finalphase.fabricapins.management.dto.ReceitaDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
    public List<ReceitaDTO> receitaAgrupada(Instant inicio, Instant fim, String agrupamentoReceita, String canal) {
        if (!AGRUPAMENTOS_VALIDOS.contains(agrupamentoReceita)) {
            throw new IllegalArgumentException("Agrupamento inválido: " + agrupamentoReceita);
        }

        String sql = """
                SELECT
                    DATE_TRUNC('%s', p.data_pagamento_confirmado) as periodo,
                    SUM(p.valor_total_final) as total
                FROM tb_pedido p
                WHERE p.status_pedido <> 'CANCELADO'
                    AND p.data_pagamento_confirmado BETWEEN :inicio AND :fim
                    AND (:canal IS NULL OR p.origem_pedido = :canal)
                GROUP BY periodo
                ORDER BY periodo
                """.formatted(agrupamentoReceita);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setParameter("canal", canal)
                .getResultList();

        return rows.stream().map(r -> {
            Instant periodo = r[0] instanceof OffsetDateTime odt
                    ? odt.toInstant()
                    : ((java.sql.Timestamp) r[0]).toInstant();
            BigDecimal total = r[1] instanceof BigDecimal bd ? bd : BigDecimal.valueOf(((Number) r[1]).doubleValue());
            return new ReceitaDTO(periodo, null, total);
        }).toList();
    }

    @Override
    public List<ProducaoDTO> producaoAgrupada(Instant inicio, Instant fim, String canal, String agrupamentoProducao, Long produtoId, Long variacaoId, Long categoriaId) {


        String sql = """
                SELECT
                    CASE
                        WHEN :agrupamento = 'PRODUTO' THEN COALESCE(pr.nome, 'SEM_PRODUTO')
                        WHEN :agrupamento = 'VARIACAO' THEN COALESCE(pv.nome, 'SEM_VARIACAO')
                        WHEN :agrupamento = 'CATEGORIA' THEN COALESCE(c.nome, 'SEM_CATEGORIA')
                        ELSE 'GERAL'
                    END as grupo,
                    COUNT(DISTINCT p.id) as quantidade,
                    AVG(EXTRACT(EPOCH FROM (p.data_fim_producao - p.data_inicio_producao)) /3600) as tempo_medio
                    FROM tb_pedido p
                    LEFT JOIN tb_item_pedido ip ON ip.pedido_id = p.id
                    LEFT JOIN tb_produto_variacao pv ON pv.id = ip.produto_variacao_id
                    LEFT JOIN tb_produto pr ON pr.id = pv.produto_id
                    LEFT JOIN tb_categoria c ON c.id = pr.categoria_id
                    WHERE p.data_inicio_producao IS NOT NULL
                        AND p.data_fim_producao IS NOT NULL
                        AND p.data_inicio_producao BETWEEN :inicio AND :fim
                        AND (:canal IS NULL OR p.origem_pedido = :canal)
                        AND (:produtoId IS NULL OR pr.id = :produtoId)
                        AND (:variacaoId IS NULL OR pv.id = :variacaoId)
                        AND (:categoriaId IS NULL OR c.id = :categoriaId)
                    GROUP BY grupo
                    ORDER BY tempo_medio DESC                    
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .setParameter("canal", canal)
                .setParameter("produtoId", produtoId)
                .setParameter("variacaoId", variacaoId)
                .setParameter("categoriaId", categoriaId)
                .setParameter("agrupamento", agrupamentoProducao)
                .getResultList();

        return rows.stream().map(r -> {
            String grupo = (String) r[0];
            Long quantidade = r[1] != null ? ((Number) r[1]).longValue() : 0L;
            Double tempoMedio = r[2] != null
                    ? ((Number) r[2]).doubleValue()
                    : 0;

            return new ProducaoDTO(
                    grupo,
                    tempoMedio,
                    quantidade            );
        }).toList();

    }
}
