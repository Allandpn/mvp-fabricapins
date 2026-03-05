package com.finalphase.fabricapins.dto.produto;

import com.finalphase.fabricapins.domain.enums.TipoEstoqueProduto;
import com.finalphase.fabricapins.dto.produto_variacao.ProdutoVariacaoMinDTO;
import lombok.Setter;

import java.math.BigDecimal;
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
