package com.finalphase.fabricapins.ecommerce.domain.entities;

import com.finalphase.fabricapins.ecommerce.domain.enums.StatusPedido;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tb_pedido_status_historico")
public class PedidoStatusHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pedidoId;

    @Enumerated(EnumType.STRING)
    private StatusPedido statusAnterior;

    @Enumerated(EnumType.STRING)
    private StatusPedido statusNovo;

    private Instant momento;
}
