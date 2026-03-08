package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.ProdutoVariacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutoVariacaoRepository extends JpaRepository<ProdutoVariacao, Long> {


    Optional<ProdutoVariacao> findByProdutoIdAndIdAndProdutoAtivoTrueAndAtivoTrue(Long produtoId, Long variacaoId);

    boolean existsBySku(String sku);

    Optional<List<ProdutoVariacao>> findAllByProdutoIdAndProdutoAtivoTrueAndAtivoTrue(Long produtoId);

    boolean existsBySkuAndIdNot(String sku, Long variacaoId);

    Optional<ProdutoVariacao> findByIdAndAtivoTrue(Long aLong);
}
