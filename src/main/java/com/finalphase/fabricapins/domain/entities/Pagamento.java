package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tb_pagamento")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Instant dataPagamento;

    @Setter
    private Double valorPago;

    @Setter
    private Integer formaPagamento;

    @OneToOne(mappedBy = "pagamento")
    private Pedido pedido;

    public Pagamento(Instant dataPagamento, Integer formaPagamento, Double valorPago) {
        this.dataPagamento = dataPagamento;
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
    }
}
