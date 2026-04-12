package com.finalphase.fabricapins.management.service;

import com.finalphase.fabricapins.management.dto.ProducaoDTO;
import com.finalphase.fabricapins.management.dto.ProducaoRequest;
import com.finalphase.fabricapins.management.dto.ReceitaDTO;
import com.finalphase.fabricapins.management.dto.ReceitaRequest;
import com.finalphase.fabricapins.management.enums.AgrupamentoPeriodo;
import com.finalphase.fabricapins.management.repository.RelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
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
        String agrupamento = mapearAgrupamento(request.agrupamento());

        List<ReceitaDTO> result = repository.receitaAgrupada(
                request.dataInicio(),
                request.dataFim(),
                agrupamento,
                request.canal() != null ? request.canal().name() : null
        );

        return result.stream().map(x -> new ReceitaDTO(
                normalizarPeriodo(x.periodo(), request.agrupamento()),
                formatarLabel(x.periodo(), request.agrupamento()),
                x.total()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProducaoDTO> tempoProducao(ProducaoRequest request){
        List<ProducaoDTO> result = repository.producaoAgrupada(
                request.dataInicio(),
                request.dataFim(),
                request.canal() != null ? request.canal().name() : null,
                request.agrupamento().name(),
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



    private Instant normalizarPeriodo(Instant instant, AgrupamentoPeriodo agrupamento) {
        ZonedDateTime z = instant.atZone(ZONE);

        return switch (agrupamento) {
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

    private String formatarLabel(Instant instant, AgrupamentoPeriodo agrupamento) {
        Locale locale = new Locale("pt", "BR");
        ZonedDateTime z = instant.atZone(ZONE);

        return switch (agrupamento) {
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

    private String mapearAgrupamento(AgrupamentoPeriodo agrupamento) {
        return switch (agrupamento) {
            case DIA -> "day";
            case SEMANA -> "week";
            case MES -> "month";
            case TRIMESTRE -> "quarter";
            case ANO -> "year";
        };
    }

}
