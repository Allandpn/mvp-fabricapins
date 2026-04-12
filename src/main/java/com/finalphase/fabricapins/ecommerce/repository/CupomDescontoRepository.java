package com.finalphase.fabricapins.ecommerce.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.CupomDesconto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CupomDescontoRepository extends JpaRepository<CupomDesconto, Long> {

    Optional<CupomDesconto> findByCodigoAndAtivoTrue(String codigo);
}
