package com.finalphase.fabricapins.controller;

import com.finalphase.fabricapins.dto.perfil.PerfilMinDTO;
import com.finalphase.fabricapins.dto.perfil.PerfilRequest;
import com.finalphase.fabricapins.dto.perfil.PerfilWithUsuariosDTO;
import com.finalphase.fabricapins.exception.model.CustomError;
import com.finalphase.fabricapins.service.PerfilService;
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
import java.util.List;

@RestController
@RequestMapping(value = "/perfil")
@Tag(name = "Perfil", description = "Operações relacionados ao Perfil do Usuário")
public class PerfilController {

    @Autowired
    private PerfilService service;

    @Operation(summary = "Buscar perfil por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil localizado"),
            @ApiResponse(responseCode = "404", description = "Perfil não localizado", content = @Content)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<PerfilMinDTO> findById(@PathVariable Long id){
        PerfilMinDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar todos os Perfis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfis localizados"),
            @ApiResponse(responseCode = "404", description = "Nenhum Perfil localizado", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<?>> findAll(@RequestParam(defaultValue = "false") boolean withUsuarios){
        if(withUsuarios){
            List<PerfilWithUsuariosDTO> ListDto = service.findAllWithUsuarios();
            return ResponseEntity.ok(ListDto);
        }
        List<PerfilMinDTO> ListDto = service.findAll();
        return ResponseEntity.ok(ListDto);
    }

    @Operation(summary = "Inserir Perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Perfil", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PerfilMinDTO> insertPerfil(@Valid @RequestBody PerfilRequest request){
        PerfilMinDTO dto = service.insertPerfil(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Atualizar Perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PerfilMinDTO.class))),
            @ApiResponse(responseCode = "404", description = "Perfil não localizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe um Perfil com esse nome", content = @Content)
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<PerfilMinDTO> updatePerfil(@PathVariable Long id, @Valid @RequestBody PerfilRequest request){
        return ResponseEntity.ok(service.updatePerfil(id, request));
    }

    @Operation(summary = "Remover Perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil excluido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Perfil não localizado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir pois há Usuarios associados")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePerfil(@PathVariable Long id){
        service.deletePerfil(id);
        return ResponseEntity.noContent().build();
    }

}
