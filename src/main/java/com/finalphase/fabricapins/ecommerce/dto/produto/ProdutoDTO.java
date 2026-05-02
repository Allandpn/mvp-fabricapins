package com.finalphase.fabricapins.ecommerce.dto.produto;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoEstoqueProduto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProdutoDTO(

        Long id,
        String nome,
        String descricao,
        TipoEstoqueProduto tipoEstoque,
        Integer quantidadeEstoque,
        Integer estoqueMinimo,
        BigDecimal precoVarejo,
        BigDecimal precoRevenda,
        BigDecimal custoProducao,
        LocalDate dataPrevistaLancamento,
        String sku,
        String imgUrl,
        Double peso,
        Integer altura,
        Integer largura,
        Integer comprimento,
        Instant dataCadastro,
        Instant dataAtualizacao,
        boolean ativo,
        Long categoriaId,
        String categoriaNome

) {}
