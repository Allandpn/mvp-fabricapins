package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.ProdutoVariacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoVariacaoRepository extends JpaRepository<ProdutoVariacao, Long> {


    Optional<ProdutoVariacao> findByProdutoIdAndIdAndProdutoAtivoTrueAndAtivoTrue(Long produtoId, Long variacaoId);

    boolean existsBySku(String sku);

    Optional<List<ProdutoVariacao>> findAllByProdutoIdAndProdutoAtivoTrueAndAtivoTrue(Long produtoId);
}
