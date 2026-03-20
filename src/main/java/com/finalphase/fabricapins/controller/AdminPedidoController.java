package com.finalphase.fabricapins.controller;


import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoRequest;
import com.finalphase.fabricapins.dto.frete.FreteRequest;
import com.finalphase.fabricapins.dto.frete.OpcaoFreteDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
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
import java.util.List;

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
    @PreAuthorize("hasRole('ADMIN')")
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

    // inseri itens no pedido criado
    @Operation(summary = "Adiciona Items no Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao adcionar item", content = @Content)
    })
    @PostMapping(value = "/{pedidoId}/items")
    public ResponseEntity<ItemPedidoDTO> insertItemPedido(@PathVariable Long pedidoId, @Valid @RequestBody ItemPedidoRequest request){
        ItemPedidoDTO dto = pedidoService.insertItemPedido(pedidoId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    // adiciona endereco no pedido criado
    @Operation(summary = "Adiciona Endereco no Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereco adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao adcionar endereco", content = @Content)
    })
    @PostMapping(value = "/{pedidoId}/endereco")
    public ResponseEntity<PedidoMinDTO> definirEndereco(@PathVariable Long pedidoId, @Valid @RequestBody EnderecoPedidoRequest request){
        PedidoMinDTO dto = pedidoService.definirEndereco(pedidoId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    // define frete
    @Operation(summary = "Define Frete do Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Frete definido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao definir o frete", content = @Content)
    })
    @PostMapping(value = "/{pedidoId}/frete")
    public ResponseEntity<PedidoMinDTO> definirFrete(@PathVariable Long pedidoId, @Valid @RequestBody FreteRequest request){
        PedidoMinDTO dto = pedidoService.definirFrete(pedidoId, request);
        return ResponseEntity.ok(dto);
    }


    // calcula frete do pedido
    @Operation(summary = "Calcula Frete do Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Frete calculado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao calcular o frete", content = @Content)
    })
    @PostMapping(value = "/{pedidoId}/frete/opcoes")
    public ResponseEntity<List<OpcaoFreteDTO>> calcularFrete(@PathVariable Long pedidoId){
        List<OpcaoFreteDTO> dto = pedidoService.calcularFrete(pedidoId);
        return ResponseEntity.ok(dto);
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
