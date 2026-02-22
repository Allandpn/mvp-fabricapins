package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_produto_variacao")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProdutoVariacao {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank
    @Column(nullable = false, length = 100)
    private String nome;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Setter
    @Min(0)
    @NotBlank
    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Setter
    @Min(0)
    @NotBlank
    @Column(nullable = false)
    private Integer estoqueMinimo;

    @Setter
    @NotBlank
    @Column(nullable = false, length = 100)
    private String sku;

    @Setter
    private String imgUrl;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @OneToMany(mappedBy = "produtoVariacao", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<ItemPedido> itemsPedido = new ArrayList<>();

    public ProdutoVariacao(String nome, Integer quantidadeEstoque, Integer estoqueMinimo, Produto produto) {
        this.nome = nome;
        this.quantidadeEstoque = quantidadeEstoque;
        this.estoqueMinimo = estoqueMinimo;
        this.produto = produto;
    }

    // HELPERS
    public void reduzirEstoque(int quantidade){
        if(quantidade > this.getQuantidadeEstoque()){
            throw new IllegalStateException("Estoque insuficiente");
        }
        this.quantidadeEstoque -= quantidade;
    }
}
