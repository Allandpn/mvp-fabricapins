package com.finalphase.fabricapins.ecommerce.dto.produto;

import com.finalphase.fabricapins.ecommerce.dto.produto_variacao.ProdutoVariacaoMinDTO;

import java.time.Instant;
import java.util.List;

public record ProdutoDTO(
        Long id,
        String nome,
        String descricao,
        String imgUrl,
        Double peso,
        Double altura,
        Double largura,
        Double comprimento,
        String slug,
        Instant dataCadastro,
        Instant dataAtualizacao,
        boolean destaque,
        boolean ativo,
        Long categoriaId,
        String categoriaNome,
        List<ProdutoVariacaoMinDTO> produtosVariacao
) {}
