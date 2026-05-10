package com.finalphase.fabricapins.management.controller;

import com.finalphase.fabricapins.ecommerce.domain.entities.Categoria;
import com.finalphase.fabricapins.ecommerce.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.SituacaoEstoque;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.management.dto.*;
import com.finalphase.fabricapins.management.enums.AgrupamentoPeriodo;
import com.finalphase.fabricapins.management.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(value = "/gestao/relatorios")
@Tag(name = "Gestão", description = "Operações relacionadas aos relatorios de Gestão")
public class RelatorioController {

    @Autowired
    private RelatorioService service;

    // Resumo
    @Operation(summary = "Resumo dos principais indicadores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/resumo")
    public ResponseEntity<ResumoDTO> resumo(
            @RequestParam Instant dataInicio,
            @RequestParam Instant dataFim,
            @RequestParam(required = false) OrigemPedido canal,
            @RequestParam(required = false) TipoCliente tipoCliente,
            @RequestParam(required = false) Long categoriaId) {
        ResumoDTO dto = service.resumo(dataInicio, dataFim, canal, tipoCliente, categoriaId);
        return ResponseEntity.ok(dto);
    }


    // Estoque
    @Operation(summary = "Dashboard de Estoque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/estoque")
    public ResponseEntity<EstoqueDTO> resumo(
            @RequestParam Instant dataInicio,
            @RequestParam Instant dataFim,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) SituacaoEstoque situacaoEstoque) {
        EstoqueDTO dto = service.estoque(dataInicio, dataFim, categoriaId, situacaoEstoque);
        return ResponseEntity.ok(dto);
    }


    // Receita
    @Operation(summary = "Dashboard de Receita")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/receita")
    public ResponseEntity<ReceitaDTO> receita(
            @RequestParam Instant dataInicio,
            @RequestParam Instant dataFim,
            @RequestParam(required = false) OrigemPedido canal,
            @RequestParam(required = false) TipoCliente tipoCliente,
            @RequestParam(required = false) Long categoriaId) {
        ReceitaDTO dto = service.receita(dataInicio, dataFim, canal, tipoCliente, categoriaId);
        return ResponseEntity.ok(dto);
    }


    // Producao
    @Operation(summary = "Dashboard de Producao")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/producao")
    public ResponseEntity<ProducaoDTO> producao(
            @RequestParam Instant dataInicio,
            @RequestParam Instant dataFim) {
        ProducaoDTO dto = service.producao(dataInicio, dataFim);
        return ResponseEntity.ok(dto);
    }


    // Planejamento
    @Operation(summary = "Dashboard de Planejamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/planejamento")
    public ResponseEntity<PlanejamentoDTO> planejamento(
            @RequestParam Instant dataInicio,
            @RequestParam Instant dataFim,
            @RequestParam AgrupamentoPeriodo periodo,
            @RequestParam(required = false) OrigemPedido canal,
            @RequestParam(required = false) TipoCliente tipoCliente,
            @RequestParam(required = false) Long categoriaId) {
        PlanejamentoDTO dto = service.planejamento(dataInicio, dataFim, periodo, canal, tipoCliente, categoriaId);
        return ResponseEntity.ok(dto);
    }











//    // Relatorio Analítico de Receitas
//    @Operation(summary = "Relatórios de Receita por Período")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
//            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
//    })
//    @GetMapping("/receita")
//    public ResponseEntity<List<ReceitaDTO>> receita(@Valid ReceitaRequest request) {
//        List<ReceitaDTO> dto = service.receita(request);
//        return ResponseEntity.ok(dto);
//    }
//
//    // Relatorio Analítico de Tempo de Producao
//    @Operation(summary = "Relatórios de Producao por Período")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
//            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
//    })
//    @GetMapping("/producao")
//    public ResponseEntity<List<ProducaoDTO>> producao(@Valid ProducaoRequest request) {
//        List<ProducaoDTO> dto = service.tempoProducao(request);
//        return ResponseEntity.ok(dto);
//    }
//
//
//    @Operation(summary = "Relatórios de Volume de Vendas por Período")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
//            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
//    })
//    @GetMapping("/vendas")
//    public ResponseEntity<List<VendasDTO>> vendas(@Valid VendasRequest request) {
//        List<VendasDTO> dto = service.vendas(request);
//        return ResponseEntity.ok(dto);
//    }
//
//
//    @Operation(summary = "Relatórios de Estoque e Demanda por Periodo")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
//            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
//    })
//    @GetMapping("/estoque")
//    public ResponseEntity<List<EstoqueDTO>> estoque(@Valid EstoqueRequest request) {
//        List<EstoqueDTO> dto = service.estoque(request);
//        return ResponseEntity.ok(dto);
//    }
}
