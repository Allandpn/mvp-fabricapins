package com.finalphase.fabricapins.ecommerce.controller;


import com.finalphase.fabricapins.ecommerce.dto.categoria.CategoriaDTO;
import com.finalphase.fabricapins.ecommerce.dto.categoria.CategoriaRequest;
import com.finalphase.fabricapins.ecommerce.service.CategoriaService;
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
@RequestMapping(value = "/categorias")
@Tag(name = "Categoria", description = "Operações relacionados a Categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService service;

    @Operation(summary = "Buscar Categoria por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria localizada"),
            @ApiResponse(responseCode = "404", description = "Categoria não localizada", content = @Content)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoriaDTO> findById(@PathVariable Long id){
        CategoriaDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar todos as Categorias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias localizadas"),
            @ApiResponse(responseCode = "404", description = "Nenhum Categoria localizada", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<CategoriaDTO>> findAll(){
        List<CategoriaDTO> ListDto = service.findAll();
        return ResponseEntity.ok(ListDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inserir Categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar a Categoria", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoriaDTO> insertCategoria(@Valid @RequestBody CategoriaRequest request){
        CategoriaDTO dto = service.insertCategoria(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar Categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoriaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não localizada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe uma Categoria com esse nome", content = @Content)
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoriaDTO> updateCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request){
        return ResponseEntity.ok(service.updateCategoria(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover Categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria excluida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não localizada"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir pois existe alguma entidade associada")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Long id){
        service.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
