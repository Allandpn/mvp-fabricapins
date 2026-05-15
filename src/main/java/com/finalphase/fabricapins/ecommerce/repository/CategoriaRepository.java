package com.finalphase.fabricapins.ecommerce.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.Categoria;
import com.finalphase.fabricapins.ecommerce.domain.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends
        JpaRepository<Categoria, Long>
{

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);

    Optional<Categoria> findByIdAndAtivaTrue(Long id);

    List<Categoria> findAllByAtivaTrue();
}
