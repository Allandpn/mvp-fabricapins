package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tb_opcao_frete_pedido")
public class OpcaoFretePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String serviceId;

    @Setter
    private String nome;

    @Setter
    private BigDecimal valor;

    @Setter
    private Integer prazoDias;

    @Setter
    private String provider;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    public OpcaoFretePedido(String serviceId, String nome, BigDecimal valor, Integer prazoDias, String provider) {
        this.serviceId = serviceId;
        this.nome = nome;
        this.valor = valor;
        this.prazoDias = prazoDias;
        this.provider = provider;
    }
}
