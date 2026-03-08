package com.finalphase.fabricapins.dto.endereco;

import io.swagger.v3.oas.annotations.media.Schema;

public record EnderecoPedidoDTO (
        @Schema(example = "12345678")
        String cep,
        @Schema(example = "SP")
        String estado,
        @Schema(example = "Sao Paulo")
        String cidade,
        @Schema(example = "Centro")
        String bairro,
        @Schema(example = "Rua Sao Paulo")
        String logradouro,
        @Schema(example = "100")
        String numero,
        @Schema(example = "casa 03")
        String complemento,
        @Schema(example = "Proximo Praça da Sé")
        String pontoReferencia
    ) {}
