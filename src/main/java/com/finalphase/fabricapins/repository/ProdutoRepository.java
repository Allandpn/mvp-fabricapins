package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
