package com.finalphase.fabricapins.ecommerce.controller;


import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.ecommerce.exception.model.CustomError;
import com.finalphase.fabricapins.ecommerce.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/clientes/{clienteId}/enderecos")
@Tag(name = "Endereco", description = "Gerenciamento de endereços do cliente")
public class EnderecoController {

    private final EnderecoService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Buscar endereco por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereco localizado"),
            @ApiResponse(responseCode = "404", description = "Endereco não localizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomError.class)))
    })
    @GetMapping(value = "/{enderecoId}")
    public ResponseEntity<EnderecoDTO> findById(@PathVariable Long clienteId, @PathVariable Long enderecoId){
        EnderecoDTO dto = service.findByIdAndClienteId(clienteId, enderecoId);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Buscar Enderecos do CLiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enderecos localizados"),
            @ApiResponse(responseCode = "404", description = "Nenhum Endereco localizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomError.class)))
    })
    @GetMapping()
    public ResponseEntity<List<EnderecoDTO>> findAll(@PathVariable Long clienteId){
        List<EnderecoDTO> ListDto = service.findAllByClienteId(clienteId);
        return ResponseEntity.ok(ListDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Adiconar Endereco ao Cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereco criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Endereco",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomError.class)))
    })
    @PostMapping
    public ResponseEntity<EnderecoDTO> insertEndereco(@PathVariable Long clienteId, @Valid @RequestBody EnderecoDTO request){
        EnderecoDTO dto = service.insertEndereco(clienteId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Atualizar Endereco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereco atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnderecoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Endereco não localizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomError.class))),
            @ApiResponse(responseCode = "409", description = "Já existe um Endereco com esse nome",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomError.class)))
    })
    @PutMapping(value = "/{enderecoId}")
    public ResponseEntity<EnderecoDTO> updateEndereco(@PathVariable Long clienteId, @PathVariable Long enderecoId, @Valid @RequestBody EnderecoDTO request){
        return ResponseEntity.ok(service.updateEndereco(clienteId, enderecoId, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    @Operation(summary = "Remover Endereco")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereco excluido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereco não localizado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir pois há entidades associadas")
    })
    @DeleteMapping(value = "/{enderecoId}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable Long clienteId, @PathVariable Long enderecoId){
        service.deleteEndereco(clienteId, enderecoId);
        return ResponseEntity.noContent().build();
    }
}
