package com.finalphase.fabricapins.analytics.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatorioRepository extends JpaRepository<Pedido, Long>, RelatorioRepositoryCustom {
}
