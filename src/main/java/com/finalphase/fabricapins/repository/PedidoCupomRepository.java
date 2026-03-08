package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.PedidoCupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoCupomRepository extends JpaRepository<PedidoCupom, Long>{

    long countByCupomDescontoId(Long cupomId);
}
