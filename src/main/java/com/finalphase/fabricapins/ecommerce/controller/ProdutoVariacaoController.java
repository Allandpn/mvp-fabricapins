package com.finalphase.fabricapins.ecommerce.controller;

import com.finalphase.fabricapins.ecommerce.dto.produto_variacao.CatalogoProdutoVariacaoDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto_variacao.ProdutoVariacaoDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto_variacao.ProdutoVariacaoRequest;
import com.finalphase.fabricapins.ecommerce.service.ProdutoVariacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/produtos/{produtoId}/variacoes")
@Tag(name = "Variação do Produto", description = "Operações relacionados às Variações do Produto")
public class ProdutoVariacaoController {

    @Autowired
    private ProdutoVariacaoService service;

    @Operation(summary = "Buscar Produto por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto localizado"),
            @ApiResponse(responseCode = "404", description = "Produto não localizado", content = @Content)
    })
    @GetMapping(value = "/{variacaoId}")
    public ResponseEntity<ProdutoVariacaoDTO> findById(@PathVariable Long produtoId, @PathVariable Long variacaoId){
        ProdutoVariacaoDTO dto = service.findById(produtoId, variacaoId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar todos os Produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos localizados"),
            @ApiResponse(responseCode = "404", description = "Nenhum Produto localizado", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<CatalogoProdutoVariacaoDTO>> findAll(@PathVariable Long produtoId){
        List<CatalogoProdutoVariacaoDTO> ListDto = service.findAll(produtoId);
        return ResponseEntity.ok(ListDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inserir Produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Produto", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProdutoVariacaoDTO> insertProduto(@PathVariable Long produtoId, @Valid @RequestBody ProdutoVariacaoRequest request){
        ProdutoVariacaoDTO dto = service.insertProduto(produtoId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar Produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProdutoVariacaoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produto não localizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe um Produto com esse nome", content = @Content)
    })
    @PutMapping(value = "/{variacaoId}")
    public ResponseEntity<ProdutoVariacaoDTO> updateProduto(@PathVariable Long produtoId,
                                                            @PathVariable Long variacaoId,
                                                            @Valid @RequestBody ProdutoVariacaoRequest request){
        return ResponseEntity.ok(service.updateProduto(produtoId, variacaoId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover Produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não localizado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir pois existe alguma entidade associada")
    })
    @DeleteMapping(value = "/{variacaoId}")
    public ResponseEntity<Void> deleteProduto(@PathVariable Long produtoId, @PathVariable Long variacaoId){
        service.deleteProduto(produtoId, variacaoId);
        return ResponseEntity.noContent().build();
    }
}
