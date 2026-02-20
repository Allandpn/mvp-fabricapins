package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_pedido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Instant dataCriacao;

    @Setter
    private Integer statusPedido;

    @Setter
    private Double valorTotal;

    @Setter
    private Double desconto;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToOne
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;

    @OneToMany(mappedBy = "pedido")
    private List<ItemPedido> itemsPedido = new ArrayList<>();

    public Pedido(Instant dataCriacao, Integer statusPedido, Double valorTotal, Double desconto) {
        this.dataCriacao = dataCriacao;
        this.statusPedido = statusPedido;
        this.valorTotal = valorTotal;
        this.desconto = desconto;
    }
}
