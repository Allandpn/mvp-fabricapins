package com.finalphase.fabricapins.controller;


import com.finalphase.fabricapins.dto.cliente.ClienteMinDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteRequest;
import com.finalphase.fabricapins.dto.produto.ProdutoDTO;
import com.finalphase.fabricapins.dto.produto.ProdutoMinDTO;
import com.finalphase.fabricapins.dto.produto.ProdutoRequest;
import com.finalphase.fabricapins.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/produtos")
@Tag(name = "Produto", description = "Operações relacionados ao Produto")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @Operation(summary = "Buscar Produto por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto localizado"),
            @ApiResponse(responseCode = "404", description = "Produto não localizado", content = @Content)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<ProdutoDTO> findById(@PathVariable Long id){
        ProdutoDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar todos os Produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos localizados"),
            @ApiResponse(responseCode = "404", description = "Nenhum Produto localizado", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<Page<ProdutoMinDTO>> findAll(Pageable pageable){
        Page<ProdutoMinDTO> ListDto = service.findAll(pageable);
        return ResponseEntity.ok(ListDto);
    }

    @Operation(summary = "Inserir Produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Produto", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProdutoMinDTO> insertProduto(@Valid @RequestBody ProdutoRequest request){
        ProdutoMinDTO dto = service.insertProduto(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Atualizar Produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoMinDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto não localizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe um Produto com esse nome", content = @Content)
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<ProdutoMinDTO> updateProduto(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request){
        return ResponseEntity.ok(service.updateProduto(id, request));
    }

    @Operation(summary = "Remover Produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não localizado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir pois existe alguma entidade associada")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long id){
        service.deleteProduto(id);
        return ResponseEntity.noContent().build();
    }

}


