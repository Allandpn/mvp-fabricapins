package com.finalphase.fabricapins.ecommerce.dto.produto;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoEstoqueProduto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ProdutoAdminDTO(

        Long id,
        String nome,
        String imgUrl,
        String categoriaNome,
        String sku,
        Integer quantidadeEstoque,
        Integer estoqueMinimo,
        boolean ativo,
        BigDecimal precoVarejo
) {}
