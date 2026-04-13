package com.finalphase.fabricapins.management.service;

import com.finalphase.fabricapins.ecommerce.exception.BusinessException;
import com.finalphase.fabricapins.management.dto.*;
import com.finalphase.fabricapins.management.enums.AgrupamentoPeriodo;
import com.finalphase.fabricapins.management.repository.RelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Locale;

@Service
public class RelatorioService {

    private static final ZoneId ZONE = ZoneOffset.UTC;

    @Autowired
    private RelatorioRepository repository;

    @Transactional(readOnly = true)
    public List<ReceitaDTO> receita(ReceitaRequest request){
        String periodo = mapearAgrupamento(request.periodo());

        List<ReceitaDTO> result = repository.receitaAgrupada(
                request.dataInicio(),
                request.dataFim(),
                periodo,
                request.canal() != null ? request.canal().name() : null
        );

        return result.stream().map(x -> new ReceitaDTO(
                normalizarPeriodo(x.periodo(), request.periodo()),
                formatarLabel(x.periodo(), request.periodo()),
                x.total()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProducaoDTO> tempoProducao(ProducaoRequest request){
        List<ProducaoDTO> result = repository.producaoAgrupada(
                request.dataInicio(),
                request.dataFim(),
                request.canal() != null ? request.canal().name() : null,
                request.dimensao().name(),
                request.produtoId(),
                request.produtoVariacaoId(),
                request.categoriaId()
        );

        return result.stream().map(x -> new ProducaoDTO(
                x.grupo(),
                x.tempoMedioHoras(),
                x.quantidadePedidos()
        )).toList();
    }


    @Transactional(readOnly = true)
    public List<VolumeVendasDTO> volume(VolumeVendasRequest request){
        String periodo = mapearAgrupamento(request.periodo());
        List<Object[]> rows = repository.volumeAgrupado(
                request.dataInicio(),
                request.dataFim(),
                request.canal() != null ? request.canal().name() : null,
                periodo,
                request.dimensao().name(),
                request.produtoId(),
                request.produtoVariacaoId(),
                request.categoriaId()
        );
        return rows.stream().map(r -> {
            Instant periodoRaw = r[0] instanceof OffsetDateTime odt
                    ? odt.toInstant()
                    : ((java.sql.Timestamp) r[0]).toInstant();
            String grupo = r[1] != null ? r[1].toString() : "SEM_DADO";
            Long pedidos = r[2] != null ? ((Number) r[2]).longValue() : 0L;
            Long itens = r[3] != null ? ((Number) r[3]).longValue() : 0L;
            Double receita = r[4] != null ? ((Number) r[4]).doubleValue() : 0.0;
            return new VolumeVendasDTO(
                    normalizarPeriodo(periodoRaw, request.periodo()),
                    formatarLabel(periodoRaw, request.periodo()),
                    grupo,
                    pedidos,
                    itens,
                    receita
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<EstoqueDTO> estoque(EstoqueRequest request){
        Instant[] periodo = resolverPeriodoDemanda(
                request.demandaInicio(),
                request.demandaFim()
        );
        List<Object[]> rows = repository.estoqueAnalitico(
                request.dimensao().name(),
                request.produtoId(),
                request.produtoVariacaoId(),
                request.categoriaId(),
                periodo[0],
                periodo[1]
        );
        return rows.stream().map(r -> {
            String grupo = r[0] != null ? r[0].toString() : "SEM_DADO";
            Integer quantidade = r[1] != null ? ((Number) r[1]).intValue() : 0;
            Integer minimo = r[2] != null ? ((Number) r[2]).intValue() : 0;
            Long demanda = r[3] != null ? ((Number) r[3]).longValue() : 0L;
            String status = calcularStatus(quantidade, minimo);
            return new EstoqueDTO(
                    grupo,
                    quantidade,
                    minimo,
                    status,
                    demanda
            );
        }).toList();
    }


    private Instant[] resolverPeriodoDemanda(Instant inicio, Instant fim) {
        if (inicio == null && fim == null) {
            Instant agora = Instant.now();
            Instant inicioDefault = agora.minus(30, ChronoUnit.DAYS);
            return new Instant[]{inicioDefault, agora};
        }
        if (inicio == null || fim == null) {
            throw new BusinessException(
                    "Para cálculo de demanda, informe dataInicio e dataFim ou nenhum dos dois"
            );
        }
        return new Instant[]{inicio, fim};
    }

    private String calcularStatus(int quantidade, int minimo) {
        return quantidade <= minimo ? "CRITICO" : "OK";
    }

    private Instant normalizarPeriodo(Instant instant, AgrupamentoPeriodo periodo) {
        ZonedDateTime z = instant.atZone(ZONE);

        return switch (periodo) {
            case DIA -> z.toLocalDate().atStartOfDay(ZONE).toInstant();

            case SEMANA -> z
                    .with(DayOfWeek.MONDAY)
                    .toLocalDate()
                    .atStartOfDay(ZONE)
                    .toInstant();

            case MES -> z
                    .withDayOfMonth(1)
                    .toLocalDate()
                    .atStartOfDay(ZONE)
                    .toInstant();

            case TRIMESTRE -> {
                int quarterStartMonth = ((z.getMonthValue() - 1) / 3) * 3 + 1;
                yield z
                        .withMonth(quarterStartMonth)
                        .withDayOfMonth(1)
                        .toLocalDate()
                        .atStartOfDay(ZONE)
                        .toInstant();
            }

            case ANO -> z
                    .withDayOfYear(1)
                    .toLocalDate()
                    .atStartOfDay(ZONE)
                    .toInstant();
        };
    }

    private String formatarLabel(Instant instant, AgrupamentoPeriodo periodo) {
        Locale locale = new Locale("pt", "BR");
        ZonedDateTime z = instant.atZone(ZONE);

        return switch (periodo) {
            case DIA -> z.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            case SEMANA -> {
                int week = z.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                int year = z.get(IsoFields.WEEK_BASED_YEAR);
                yield week + "/" + year;
            }

            case MES -> {
                String mes = z.getMonth()
                    .getDisplayName(TextStyle.SHORT, locale)
                        .replace(".", "");
                yield mes.substring(0, 1).toUpperCase() + mes.substring(1) + "/" + z.getYear();
            }

            case TRIMESTRE -> {
                int quarter = (z.getMonthValue() - 1) / 3 + 1;
                yield "T" + quarter + "/" + z.getYear();
            }

            case ANO -> String.valueOf(z.getYear());
        };
    }

    private String mapearAgrupamento(AgrupamentoPeriodo periodo) {
        return switch (periodo) {
            case DIA -> "day";
            case SEMANA -> "week";
            case MES -> "month";
            case TRIMESTRE -> "quarter";
            case ANO -> "year";
        };
    }

}
