package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);

    Optional<Categoria> findByIdAndAtivaTrue(Long id);

    List<Categoria> findAllByAtivaTrue();
}
