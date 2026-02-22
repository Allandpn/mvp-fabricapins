package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
