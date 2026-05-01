package com.finalphase.fabricapins.ecommerce.controller;

import com.finalphase.fabricapins.ecommerce.dto.cupom_desconto.CupomDescontoDTO;
import com.finalphase.fabricapins.ecommerce.dto.cupom_desconto.CupomDescontoRequest;
import com.finalphase.fabricapins.ecommerce.service.CupomDescontoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/cupons")
@Tag(name = "Cupom de desconto", description = "Operações relacionadas a cupons de desconto")
public class CupomDescontoController {

    private final CupomDescontoService service;

    public CupomDescontoController(CupomDescontoService service) {
        this.service = service;
    }

    @Operation(summary = "Buscar cupom por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupom localizado"),
            @ApiResponse(responseCode = "404", description = "Cupom não localizado", content = @Content)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<CupomDescontoDTO> findById(@PathVariable Long id) {
        CupomDescontoDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar todos os cupons ativos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupons localizados"),
            @ApiResponse(responseCode = "404", description = "Nenhum cupom localizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<CupomDescontoDTO>> findAll() {
        List<CupomDescontoDTO> listDto = service.findAll();
        return ResponseEntity.ok(listDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inserir cupom de desconto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o cupom", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CupomDescontoDTO> insertCupom(@Valid @RequestBody CupomDescontoRequest request) {
        CupomDescontoDTO dto = service.insertCupom(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar cupom de desconto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupom atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CupomDescontoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cupom não localizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe um cupom com esse código", content = @Content)
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<CupomDescontoDTO> updateCupom(
            @PathVariable Long id,
            @Valid @RequestBody CupomDescontoRequest request) {
        return ResponseEntity.ok(service.updateCupom(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover cupom de desconto (inativação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cupom inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cupom não localizado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir pois existe alguma entidade associada")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteCupom(@PathVariable Long id) {
        service.deleteCupom(id);
        return ResponseEntity.noContent().build();
    }
}
