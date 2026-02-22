package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.FormaPagamento;
import com.finalphase.fabricapins.domain.enums.StatusPagamento;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "tb_pagamento")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pagamento {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Instant dataPagamento;

    @Setter
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPago;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento statusPagamento;

    @Setter
    private String codigoTransacao;

    @Setter
    private String gatewayPagamento;

    @Setter
    private Integer parcelasCartao;

    @Setter
    private Instant dataConfirmacao;

    @Setter
    private String motivoRecusa;

    @OneToOne(mappedBy = "pagamento")
    private Pedido pedido;

    public Pagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
        this.statusPagamento = StatusPagamento.PENDENTE;
    }
}
