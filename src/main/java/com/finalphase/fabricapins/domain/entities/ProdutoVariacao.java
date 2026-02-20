package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_produto_variacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProdutoVariacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String nome;

    @Setter
    private Integer quantidadeEstoque;

    @Setter
    private Integer estoqueMinimo;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @OneToMany(mappedBy = "produtoVariacao")
    private List<ItemPedido> itemsPedido = new ArrayList<>();

    public ProdutoVariacao(String nome, Integer quantidadeEstoque, Integer estoqueMinimo) {
        this.nome = nome;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
    }
}
