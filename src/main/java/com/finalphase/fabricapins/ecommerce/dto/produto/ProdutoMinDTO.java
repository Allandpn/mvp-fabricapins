package com.finalphase.fabricapins.ecommerce.dto.produto;

public record ProdutoMinDTO (
        Long id,
        String nome,
        String imgUrl,
        String slug,
        boolean destaque
){}
