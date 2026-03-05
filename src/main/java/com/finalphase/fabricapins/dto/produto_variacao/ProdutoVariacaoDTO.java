package com.finalphase.fabricapins.dto.produto_variacao;

import com.finalphase.fabricapins.domain.enums.TipoEstoqueProduto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProdutoVariacaoDTO(
        Long id,
        String nome,
        String descricao,
        TipoEstoqueProduto tipoEstoque,
        Integer quantidadeEstoque,
        Integer estoqueMinimo,
        BigDecimal precoVarejo,
        BigDecimal precoRevenda,
        BigDecimal custoProducao,
        LocalDate dataPrevistaEnvio,
        String sku,
        String imgUrl,
        Instant dataAtualizacao,
        Long produtoId
) {}
