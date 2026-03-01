package com.finalphase.fabricapins.dto.produto;

import com.finalphase.fabricapins.domain.enums.TipoEstoqueProduto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProdutoDTO(
        Long id,
        String nome,
        String descricao,
        TipoEstoqueProduto tipoEstoque,
        BigDecimal precoVarejo,
        BigDecimal precoRevenda,
        String imgUrl,
        String sku,
        Instant dataCadastro,
        Instant dataAtualizacao,
        boolean destaque,
        boolean ativo,
        Long categoriaId
) {}
