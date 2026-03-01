package com.finalphase.fabricapins.dto.produto;
import com.finalphase.fabricapins.domain.enums.TipoEstoqueProduto;
import java.math.BigDecimal;
import java.time.Instant;

public record ProdutoRequest(
        String nome,
        String descricao,
        TipoEstoqueProduto tipoEstoque,
        BigDecimal precoVarejo,
        BigDecimal precoRevenda,
        BigDecimal custoProducao,
        String imgUrl,
        String sku,
        Instant dataCadastro,
        Instant dataAtualizacao,
        boolean destaque,
        boolean ativo,
        Long categoriaId
) {}