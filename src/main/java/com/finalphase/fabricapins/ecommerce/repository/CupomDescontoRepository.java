package com.finalphase.fabricapins.ecommerce.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.CupomDesconto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CupomDescontoRepository extends JpaRepository<CupomDesconto, Long> {

    Optional<CupomDesconto> findByCodigoAndAtivoTrue(String codigo);

    Optional<CupomDesconto> findByIdAndAtivoTrue(Long id);

    List<CupomDesconto> findAllByAtivoTrue();

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
