package com.finalphase.fabricapins.ecommerce.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PedidoRepository extends
        JpaRepository<Pedido, Long>,
        JpaSpecificationExecutor<Pedido>
{

    Optional<Pedido> findByCodigoPedido(String codigo);

    Page<Pedido> findAll(Pageable pageable);
}
