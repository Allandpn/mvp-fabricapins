package com.finalphase.fabricapins.dto.produto_variacao;

import com.finalphase.fabricapins.domain.enums.TipoEstoqueProduto;

import java.math.BigDecimal;

public record ProdutoVariacaoMinDTO (
        Long id,
        String nome,
        TipoEstoqueProduto tipoEstoque,
        BigDecimal precoVarejo,
        String sku,
        String imgUrl
){}
