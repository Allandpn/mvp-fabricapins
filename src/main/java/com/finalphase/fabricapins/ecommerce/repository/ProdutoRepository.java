package com.finalphase.fabricapins.ecommerce.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByIdAndAtivoTrue(Long id);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    Page<Produto> findAllByAtivoTrue(Pageable pageable);

    boolean existsByIdAndAtivoTrue(Long produtoId);

    List<Produto> findAllByIdInAndAtivoTrue(List<Long> id);

    @Modifying
    @Query("""
            UPDATE Produto p SET p.quantidadeEstoque = p.quantidadeEstoque - :qnt
            WHERE p.id = :id
            AND p.quantidadeEstoque >= :qnt
            """)
    int reduzirEstoque(Long id, Integer qnt);

    @Modifying
    @Query("""
            UPDATE Produto p SET p.quantidadeEstoque = p.quantidadeEstoque + :qnt
            WHERE p.id = :id
            """)
    int aumentarEstoque(Long id, Integer qnt);
}
