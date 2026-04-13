package com.finalphase.fabricapins.ecommerce.dto.produto_variacao;

import java.math.BigDecimal;

public record CatalogoProdutoVariacaoDTO (
        Long id,
        String nomeProduto,
        String nomeVariacao,
        String imgUrl,
        BigDecimal precoVarejo,
        String sku
){}
