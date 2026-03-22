package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Parametro;
import com.finalphase.fabricapins.domain.enums.ParametroChave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParametroRepository extends JpaRepository<Parametro, Long> {

    Optional<Parametro> findByChave(ParametroChave chave);
}
