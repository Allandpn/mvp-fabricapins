package com.finalphase.fabricapins.controller;

import com.finalphase.fabricapins.dto.usuario.UsuarioDTO;
import com.finalphase.fabricapins.dto.usuario.UsuarioRequest;
import com.finalphase.fabricapins.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping(value = "/usuario")
@Tag(name = "Usuario", description = "Operações relacionados ao Usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Operation(summary = "Buscar usuario por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario localizado"),
            @ApiResponse(responseCode = "404", description = "Usuario não localizado", content = @Content)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Long id){
        UsuarioDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar todos os Usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios localizados"),
            @ApiResponse(responseCode = "404", description = "Nenhum Usuario localizado", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<UsuarioDTO>> findAll(){
        List<UsuarioDTO> ListDto = service.findAll();
        return ResponseEntity.ok(ListDto);
    }

    @Operation(summary = "Inserir Usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Usuario", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UsuarioDTO> insertUsuario(@Valid @RequestBody UsuarioRequest request){
        UsuarioDTO dto = service.insertUsuario(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Atualizar Usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario não localizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe um Usuario com esse nome", content = @Content)
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequest request){
        return ResponseEntity.ok(service.updateUsuario(id, request));
    }

    @Operation(summary = "Remover Usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario excluido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuario não localizado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir pois há Cliente ou Perfis associados")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id){
        service.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
