package com.finalphase.fabricapins.controller;

import com.finalphase.fabricapins.dto.pedido.PedidoMinDTO;
import com.finalphase.fabricapins.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/pedidos")
@Tag(name = "Pedido", description = "Operações relacionados ao Pedido")
public class PedidoController {
    //TODO - GET /pedidos/{numeroPedido}
    //TODO - VERIFICAR SE PEDIDO TERA CODIGO PARA BUSCA ENVIADO POR EMAIL CASO O CLIENTE NAO TENHA CADASTRO

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Buscar Pedido por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido localizado"),
            @ApiResponse(responseCode = "404", description = "Cliente não localizado", content = @Content)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<PedidoMinDTO> findById(@PathVariable Long id){
        PedidoMinDTO dto = pedidoService.findById(id);
        return ResponseEntity.ok(dto);
    }
}
