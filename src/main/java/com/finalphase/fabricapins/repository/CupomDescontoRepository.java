package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.CupomDesconto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.Optional;

public interface CupomDescontoRepository extends JpaRepository<CupomDesconto, Long> {

    Optional<CupomDesconto> findByCodigoAndAtivoTrue(String codigo);
}
