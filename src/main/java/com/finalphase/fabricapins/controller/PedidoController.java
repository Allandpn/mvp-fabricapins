package com.finalphase.fabricapins.controller;


import com.finalphase.fabricapins.dto.cliente.ClienteMinDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteRequest;
import com.finalphase.fabricapins.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoMinDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoRequest;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/pedidos")
@Tag(name = "Pedido", description = "Operações relacionados ao Pedido")
public class PedidoController {
    //TODO - GET /pedidos/{numeroPedido}

    @Autowired
    private PedidoService pedidoService;

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

    @Operation(summary = "Inserir Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Pedido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PedidoMinDTO> insertPedido(@Valid @RequestBody PedidoRequest request){
        PedidoMinDTO dto = pedidoService.insertPedido(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }


}
