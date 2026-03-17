package com.finalphase.fabricapins.controller;


import com.finalphase.fabricapins.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoMinDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoAdminRequest;
import com.finalphase.fabricapins.dto.pedido.PedidoRascunhoRequest;
import com.finalphase.fabricapins.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(value = "/admin/pedidos")
@Tag(name = "Pedido", description = "Operações relacionados ao Pedido")
public class AdminPedidoController {

    @Autowired
    private PedidoService pedidoService;


    // Busca de Pedidos
    @Operation(summary = "Buscar Pedido por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido localizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não localizado", content = @Content)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<PedidoDTO> findById(@PathVariable Long id){
        PedidoDTO dto = pedidoService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar Pedido por Codigo do Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido localizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não localizado", content = @Content)
    })
    @GetMapping(value = "/codigo/{codigo}")
    public ResponseEntity<PedidoDTO> findByCodigo(@PathVariable String codigo){
        PedidoDTO dto = pedidoService.findByCodigo(codigo);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Buscar todos os Pedidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos localizados"),
            @ApiResponse(responseCode = "404", description = "Nenhum Pedido localizado", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<Page<PedidoMinDTO>> findAll(Pageable pageable){
        Page<PedidoMinDTO> ListDto = pedidoService.findAll(pageable);
        return ResponseEntity.ok(ListDto);
    }


    // Inserir Novos Pedidos

    // Inserir Pedido Completo
    @Operation(summary = "Inserir Pedido Completo")
    @GetMapping(value = "/completo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Pedido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PedidoMinDTO> insertPedidoCompleto(@Valid @RequestBody PedidoAdminRequest request){
        PedidoMinDTO dto = pedidoService.insertPedidoCompleto(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }


    // Inserir Pedidos em Etapas
    @Operation(summary = "Inserir Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Pedido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PedidoMinDTO> insertPedidoRascunho(@Valid @RequestBody PedidoRascunhoRequest request){
        PedidoMinDTO dto = pedidoService.insertPedidoRascunho(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }



    // TODO - Implementar
    @Operation(summary = "Alterar Status do Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao alterar o Pedido", content = @Content)
    })
    @PatchMapping(value = "/{id}/status")
    public ResponseEntity<PedidoMinDTO> alterarStatusPedido(@Valid @RequestBody PedidoAdminRequest request){
        PedidoMinDTO dto = pedidoService.alterarStatusPedido(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }


}
