package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_item_pedido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemPedido {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Min(1)
    @Column(nullable = false)
    private Integer quantidade;

    @Setter
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal precoUnitario;

    @Setter
    @Column(nullable = false, length = 150)
    private String nomeProdutoSnapshot;

    @Setter
    private String imgProdutoSnapshot;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_variacao_id")
    private ProdutoVariacao produtoVariacao;

    public ItemPedido(Integer quantidade, BigDecimal precoUnitario, String nomeProdutoSnapshot) {
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.nomeProdutoSnapshot = nomeProdutoSnapshot;
    }

    // Calcula valores Derivados
    public BigDecimal calcularSubTotal(){
        return precoUnitario.multiply(new BigDecimal(quantidade));
    }
}
