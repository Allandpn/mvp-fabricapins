package com.finalphase.fabricapins.dto.item_pedido;

import io.swagger.v3.oas.annotations.media.Schema;

public record ItemPedidoRequest (
        @Schema(example = "1")
        Long produtoVariacaoId,
        @Schema(example = "2")
        Integer quantidade
){}
