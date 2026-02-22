package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
