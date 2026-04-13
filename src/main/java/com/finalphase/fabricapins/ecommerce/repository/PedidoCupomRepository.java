package com.finalphase.fabricapins.ecommerce.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.PedidoCupom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoCupomRepository extends JpaRepository<PedidoCupom, Long>{

    long countByCupomDescontoId(Long cupomId);
}
