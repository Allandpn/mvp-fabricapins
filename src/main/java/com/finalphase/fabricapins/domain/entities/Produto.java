package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.TipoEstoqueProduto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
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
    @NotBlank
    @Column(nullable = false, length = 150)
    private String nome;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Setter
    @NotBlank
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEstoqueProduto tipoEstoque;

    @Setter
    @NotBlank
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal precoVarejo;

    @Setter
    @NotBlank
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal precoRevenda;

    @Setter
    private String imgUrl;

    @Setter
    @NotBlank
    @Column(unique = true, length = 100)
    private String sku;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dataCadastro;

    @UpdateTimestamp
    private Instant dataAtualizacao;

    @Setter
    private boolean destaque = false;

    @Setter
    private boolean ativo = true;

    @OneToMany(mappedBy = "produto", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<ProdutoVariacao> produtosVariacao = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Produto(String nome, TipoEstoqueProduto tipoEstoque,
                   BigDecimal precoVarejo, BigDecimal precoRevenda) {
        this.nome = nome;
        this.tipoEstoque = tipoEstoque;
        this.precoVarejo = precoVarejo;
        this.precoRevenda = precoRevenda;
    }

    //HELPERS
    public void adicionarProdutoVariacao(ProdutoVariacao prodV){
        produtosVariacao.add(prodV);
        prodV.setProduto(this);
    }

    public void removerProdutoVariacao(ProdutoVariacao prodV){
        produtosVariacao.remove(prodV);
        prodV.setProduto(null);
    }

}
