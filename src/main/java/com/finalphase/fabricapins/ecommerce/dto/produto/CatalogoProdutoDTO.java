package com.finalphase.fabricapins.ecommerce.dto.produto;

import java.math.BigDecimal;

public record CatalogoProdutoDTO(
        Long id,
        String nomeProduto,
        String imgUrl,
        BigDecimal precoVarejo,
        String sku
){}
