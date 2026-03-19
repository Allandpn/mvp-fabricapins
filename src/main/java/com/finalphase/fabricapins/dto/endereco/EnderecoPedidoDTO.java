package com.finalphase.fabricapins.dto.endereco;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EnderecoPedidoDTO (
        @NotBlank(message = "Campo requerido")
        @Schema(example = "12345678")
        String cep,
        @NotBlank(message = "Campo requerido")
        @Schema(example = "SP")
        String estado,
        @NotBlank(message = "Campo requerido")
        @Schema(example = "Sao Paulo")
        String cidade,
        @NotBlank(message = "Campo requerido")
        @Schema(example = "Centro")
        String bairro,
        @NotBlank(message = "Campo requerido")
        @Schema(example = "Rua Sao Paulo")
        String logradouro,
        @NotBlank(message = "Campo requerido")
        @Schema(example = "100")
        String numero,
        @Schema(example = "casa 03")
        String complemento,
        @Schema(example = "Proximo Praça da Sé")
        String pontoReferencia
    ) {}
