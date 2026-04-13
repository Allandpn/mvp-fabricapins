package com.finalphase.fabricapins.ecommerce.dto.produto_variacao;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoEstoqueProduto;

import java.math.BigDecimal;

public record ProdutoVariacaoMinDTO (
        Long id,
        String nome,
        TipoEstoqueProduto tipoEstoque,
        BigDecimal precoVarejo,
        String sku,
        String imgUrl
){}
