package com.finalphase.fabricapins.ecommerce.domain.entities;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoEstoqueProduto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_produto")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Produto {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 150)
    private String nome;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEstoqueProduto tipoEstoque;

    @Setter
    @Column(nullable = false)
    private Integer quantidadeEstoque;

    @Setter
    @Column(nullable = false)
    private Integer estoqueMinimo;

    @Setter
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal precoVarejo;

    @Setter
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal precoRevenda;

    @Setter
    @Column(precision = 15, scale = 4, nullable = false)
    private BigDecimal custoProducao;

    @Setter
    private LocalDate dataPrevistaLancamento;

    @Setter
    @Column(nullable = false,unique = true, length = 100)
    private String sku;

    @Setter
    private String imgUrl;

    @Setter
    private Double peso;

    @Setter
    private Integer altura;

    @Setter
    private Integer largura;

    @Setter
    private Integer comprimento;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dataCadastro;

    @UpdateTimestamp
    private Instant dataAtualizacao;

    @Setter
    @Column(nullable = false)
    private boolean ativo = true;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "produto", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<ItemPedido> itemsPedido = new ArrayList<>();

    public Produto(
            String nome,
            TipoEstoqueProduto tipoEstoque,
            Integer quantidadeEstoque
    ) {
        this.nome = nome;
        this.tipoEstoque = tipoEstoque;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    // HELPERS
    public void reduzirEstoque(int quantidade){
        if(quantidade > this.getQuantidadeEstoque()){
            throw new IllegalStateException("Estoque insuficiente");
        }
        this.quantidadeEstoque -= quantidade;
    }

    public void aumentarEstoque(int quantidade) {
        this.quantidadeEstoque += quantidade;
    }

}
