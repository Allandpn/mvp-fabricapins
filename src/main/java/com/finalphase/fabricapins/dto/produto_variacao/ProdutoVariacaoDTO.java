package com.finalphase.fabricapins.dto.produto_variacao;

public record ProdutoVariacaoDTO(
        Long id,
        String nome,
        String descricao,
        Integer quantidadeEstoque,
        Integer estoqueMinimo,
        String sku,
        String imgUrl,
        Long produtoId
) {}
