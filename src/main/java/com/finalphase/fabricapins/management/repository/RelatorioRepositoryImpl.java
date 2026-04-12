package com.finalphase.fabricapins.management.repository;

import com.finalphase.fabricapins.management.projection.ReceitaProjection;
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
    public List<ReceitaProjection> receitaAgrupada(Instant inicio, Instant fim, String agrupamento, String canal) {
        if (!AGRUPAMENTOS_VALIDOS.contains(agrupamento)) {
            throw new IllegalArgumentException("Agrupamento inválido: " + agrupamento);
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
                """.formatted(agrupamento);

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
            return (ReceitaProjection) new ReceitaProjection() {
                @Override public Instant getPeriodo() { return periodo; }
                @Override public BigDecimal getTotal() { return total; }
            };
        }).toList();
    }
}
