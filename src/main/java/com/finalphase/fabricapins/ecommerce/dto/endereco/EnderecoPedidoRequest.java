package com.finalphase.fabricapins.ecommerce.dto.endereco;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoEndereco;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnderecoPedidoRequest(
        @JsonIgnore
        Long enderecoId,

        @NotBlank
        @Schema(example = "12345678")
        String cep,

        @NotBlank
        @Schema(example = "SP")
        String estado,

        @NotBlank
        @Schema(example = "Sao Paulo")
        String cidade,

        @NotBlank
        @Schema(example = "Centro")
        String bairro,

        @NotBlank
        @Schema(example = "Rua Sao Paulo")
        String logradouro,

        @NotBlank
        @Schema(example = "100")
        String numero,

        @Schema(example = "casa 03")
        String complemento,

        @Schema(example = "Proximo Praça da Sé")
        String pontoReferencia,

        @Schema(example = "Proximo Praça da Sé")
        String observacoes,

        @NotNull
        @Schema(description = "Define se é o principal endereco de entrega")
        boolean enderecoPrincipal,

        @Schema(example = "ENTREGA")
        TipoEndereco tipoEndereco,

        @Schema(example = "Minha casa")
        String apelido
    ) {}
