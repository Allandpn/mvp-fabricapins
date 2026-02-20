package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_produto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String nome;

    @Setter
    private Integer tipoEstoque;

    @Setter
    private Double precoVarejo;

    @Setter
    private Double precoRevenda;

    @Setter
    private Integer isAtivo;

    @OneToMany(mappedBy = "produto")
    private List<ProdutoVariacao> produtosVariacao = new ArrayList<>();

    public Produto(String nome, Integer tipoEstoque, Double precoVarejo, Double precoRevenda, Integer isAtivo) {
        this.nome = nome;
        this.tipoEstoque = tipoEstoque;
        this.precoVarejo = precoVarejo;
        this.precoRevenda = precoRevenda;
        this.isAtivo = isAtivo;
    }
}
