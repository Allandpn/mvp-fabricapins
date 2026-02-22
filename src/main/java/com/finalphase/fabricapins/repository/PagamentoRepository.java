package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
