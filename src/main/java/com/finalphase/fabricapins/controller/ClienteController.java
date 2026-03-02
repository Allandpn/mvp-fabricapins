package com.finalphase.fabricapins.controller;

import com.finalphase.fabricapins.dto.cliente.ClienteMinDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteRequest;
import com.finalphase.fabricapins.dto.cliente.ClienteWtihPedidoDTO;
import com.finalphase.fabricapins.service.ClienteService;
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
@RequestMapping(value = "/cliente")
@Tag(name = "Cliente", description = "Operações relacionados ao Cliente")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @Operation(summary = "Buscar cliente por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente localizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não localizado", content = @Content)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<ClienteWtihPedidoDTO> findById(@PathVariable Long id){
        ClienteWtihPedidoDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar todos os Clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clientes localizados"),
            @ApiResponse(responseCode = "404", description = "Nenhum Cliente localizado", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<List<ClienteMinDTO>> findAll(){
        List<ClienteMinDTO> ListDto = service.findAll();
        return ResponseEntity.ok(ListDto);
    }

    @Operation(summary = "Inserir Cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Cliente", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ClienteMinDTO> insertCliente(@Valid @RequestBody ClienteRequest request){
        ClienteMinDTO dto = service.insertCliente(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Atualizar Cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteMinDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não localizado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Já existe um Cliente com esse nome", content = @Content)
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<ClienteMinDTO> updateCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest request){
        return ResponseEntity.ok(service.updateCliente(id, request));
    }

    @Operation(summary = "Remover Cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente excluido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não localizado"),
            @ApiResponse(responseCode = "409", description = "Não é possível excluir pois existe alguma entidade associada")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id){
        service.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }
}
