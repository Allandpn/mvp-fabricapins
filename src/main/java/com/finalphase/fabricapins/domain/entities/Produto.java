package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @Column(nullable = false, length = 150)
    private String nome;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Setter
    private String imgUrl;

    @Setter
    @Column(nullable = false)
    private Double peso;

    @Setter
    private Double altura;

    @Setter
    private Double largura;

    @Setter
    private Double comprimento;

    @Setter
    @NotBlank
    @Column(nullable = false,unique = true, length = 150)
    private String slug;

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

    public Produto(String nome, String descricao, String imgUrl, String slug, boolean destaque) {
        this.nome = nome;
        this.descricao = descricao;
        this.imgUrl = imgUrl;
        this.slug = slug;
        this.destaque = destaque;
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
