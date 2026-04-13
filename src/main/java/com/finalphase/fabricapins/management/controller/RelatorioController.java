package com.finalphase.fabricapins.management.controller;

import com.finalphase.fabricapins.management.dto.*;
import com.finalphase.fabricapins.management.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(value = "/gestao/relatorio")
@Tag(name = "Gestão", description = "Operações relacionadas aos relatorios de Gestão")
public class RelatorioController {

    @Autowired
    private RelatorioService service;

    // Relatorio Analítico de Receitas
    @Operation(summary = "Relatórios de Receita por Período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/receita")
    public ResponseEntity<List<ReceitaDTO>> receita(@Valid ReceitaRequest request) {
        List<ReceitaDTO> dto = service.receita(request);
        return ResponseEntity.ok(dto);
    }

    // Relatorio Analítico de Tempo de Producao
    @Operation(summary = "Relatórios de Producao por Período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/producao")
    public ResponseEntity<List<ProducaoDTO>> producao(@Valid ProducaoRequest request) {
        List<ProducaoDTO> dto = service.tempoProducao(request);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Relatórios de Volume de Vendas por Período")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/vendas")
    public ResponseEntity<List<VendasDTO>> vendas(@Valid VendasRequest request) {
        List<VendasDTO> dto = service.vendas(request);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Relatórios de Estoque e Demanda por Periodo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados localizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Erro ao buscar dados", content = @Content)
    })
    @GetMapping("/estoque")
    public ResponseEntity<List<EstoqueDTO>> estoque(@Valid EstoqueRequest request) {
        List<EstoqueDTO> dto = service.estoque(request);
        return ResponseEntity.ok(dto);
    }
}
