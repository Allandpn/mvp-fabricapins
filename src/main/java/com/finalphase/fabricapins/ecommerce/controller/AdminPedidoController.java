package com.finalphase.fabricapins.ecommerce.controller;


import com.finalphase.fabricapins.ecommerce.dto.PedidoCupom.CupomRequest;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoPedidoRequest;
import com.finalphase.fabricapins.ecommerce.dto.frete.FreteRequest;
import com.finalphase.fabricapins.ecommerce.dto.frete.OpcaoFreteDTO;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.QuantidadeItemRequest;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoAdminRequest;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoMinDTO;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoRascunhoRequest;
import com.finalphase.fabricapins.ecommerce.service.PedidoService;
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
@RequestMapping(value = "/admin/pedido")
@Tag(name = "Pedido", description = "Operações relacionados ao Pedido feito pelo Admin")
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
    //TODO - Corrigir rota
    @Operation(summary = "Inserir Pedido Completo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar o Pedido", content = @Content)
    })
    @PostMapping(value = "/completo")
    public ResponseEntity<PedidoDTO> insertPedidoCompleto(@Valid @RequestBody PedidoAdminRequest request){
        PedidoDTO dto = pedidoService.insertPedidoCompleto(request);
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

    // inseri itens no pedido criado
    @Operation(summary = "Adiciona Items no Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao adcionar item", content = @Content)
    })
    @PostMapping(value = "/{pedidoId}/items")
    public ResponseEntity<PedidoDTO> adicionarItemPedido(@PathVariable Long pedidoId, @Valid @RequestBody ItemPedidoRequest request){
        PedidoDTO dto = pedidoService.adicionarItemPedido(pedidoId, request);
        return ResponseEntity.ok(dto);
    }

    // inseri itens no pedido criado
    @Operation(summary = "Altera Items no Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao alterar item", content = @Content)
    })
    @PatchMapping(value = "/{pedidoId}/items/{itemId}")
    public ResponseEntity<PedidoDTO> alterarItemPedido(@PathVariable Long pedidoId, @PathVariable Long itemId, @Valid @RequestBody QuantidadeItemRequest request){
        PedidoDTO dto = pedidoService.alterarItemPedido(pedidoId, itemId, request.quantidade());
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Remove Items no Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao remover item", content = @Content)
    })
    @DeleteMapping(value = "/{pedidoId}/items/{itemId}")
    public ResponseEntity<PedidoDTO> removerItemPedido(@PathVariable Long pedidoId, @PathVariable Long itemId){
        PedidoDTO dto = pedidoService.removerItemPedido(pedidoId, itemId);
        return ResponseEntity.ok(dto);
    }

    // adiciona endereco no pedido criado
    @Operation(summary = "Adiciona Endereco no Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereco adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao adcionar endereco", content = @Content)
    })
    @PostMapping(value = "/{pedidoId}/endereco")
    public ResponseEntity<PedidoDTO> definirEndereco(@PathVariable Long pedidoId, @Valid @RequestBody EnderecoPedidoRequest request){
        PedidoDTO dto = pedidoService.definirEndereco(pedidoId, request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }


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

    @Operation(summary = "Define Frete do Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Frete definido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao definir o frete", content = @Content)
    })
    @PatchMapping(value = "/{pedidoId}/frete")
    public ResponseEntity<PedidoDTO> definirFrete(@PathVariable Long pedidoId, @Valid @RequestBody FreteRequest request){
        PedidoDTO dto = pedidoService.definirFrete(pedidoId, request);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Adicionar Cupom ao Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupom adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao adicionar cupom", content = @Content)
    })
    @PostMapping(value = "/{pedidoId}/cupons")
    public ResponseEntity<PedidoDTO> adicionarCupom(@PathVariable Long pedidoId, @Valid @RequestBody CupomRequest request){
        PedidoDTO dto = pedidoService.adicionarCupom(pedidoId, request);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Remover Cupom do Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupom removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao remover cupom", content = @Content)
    })
    @DeleteMapping(value = "/{pedidoId}/cupons/{codigo}")
    public ResponseEntity<PedidoDTO> removerCupom(@PathVariable Long pedidoId, @PathVariable String codigo){
        PedidoDTO dto = pedidoService.removerCupom(pedidoId, codigo);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Confirmar Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido confirmado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao confirmar pedido", content = @Content)
    })
    @PostMapping(value = "/{pedidoId}/confirmar")
    public ResponseEntity<PedidoDTO> confirmarPedido(@PathVariable Long pedidoId){
        PedidoDTO dto = pedidoService.confirmarPedido(pedidoId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Cancelar Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao cancelar pedido", content = @Content)
    })
    @DeleteMapping(value = "/{pedidoId}/cancelar")
    public ResponseEntity<PedidoMinDTO> cancelarPedido(@PathVariable Long pedidoId){
        PedidoMinDTO dto = pedidoService.cancelarPedido(pedidoId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Confirmar Pagamento do Pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pagamento confirmado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao confirmar pagamento", content = @Content)
    })
    @PatchMapping(value = "/{pedidoId}/pagamento/confirmar")
    public ResponseEntity<PedidoMinDTO> confirmarPagamento(@PathVariable Long pedidoId){
        PedidoMinDTO dto = pedidoService.confirmarPagamento(pedidoId);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Enviar Pedido para Produção")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao envair pedido", content = @Content)
    })
    @PatchMapping(value = "/{pedidoId}/producao/enviar")
    public ResponseEntity<PedidoMinDTO> enviarProducao(@PathVariable Long pedidoId){
        PedidoMinDTO dto = pedidoService.enviarProducao(pedidoId);
        return ResponseEntity.ok(dto);
    }

    
}
