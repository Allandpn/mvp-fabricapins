package com.finalphase.fabricapins.dto.produto_variacao;

import java.math.BigDecimal;

public record CatalogoProdutoVariacaoDTO (
        Long id,
        String nomeProduto,
        String nomeVariacao,
        String imgUrl,
        BigDecimal precoVarejo,
        String sku
){}
