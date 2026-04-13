package com.finalphase.fabricapins.ecommerce.controller;

import com.finalphase.fabricapins.ecommerce.dto.endereco.CepOrigemRequest;
import com.finalphase.fabricapins.ecommerce.service.ParametroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping(value = "/admin/parametros")
@Tag(name = "Parametos", description = "Operações de definição de Parametros")
public class AdminParamentoController {

    @Autowired
    private ParametroService service;

    // Busca de Pedidos
    @Operation(summary = "Altera Cep de Origem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cep alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cep não alterado", content = @Content)
    })
    @PutMapping(value = "/cep-origem")
    public ResponseEntity<Void> atualizarCepOrigem(@Valid @RequestBody CepOrigemRequest request){
        service.atualizarCepOrigem(request.cep());
        return ResponseEntity.noContent().build();
    }
}
