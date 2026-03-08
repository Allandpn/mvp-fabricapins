package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.TipoDesconto;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tb_cupom_desconto")
public class CupomDesconto {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String codigo;

    @Setter
    @Column(nullable = false)
    private boolean ativo = true;

    @Setter
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDesconto;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDesconto tipoDesconto;

    @Setter
    private LocalDate dataValidade;

    @Setter
    private Integer quantidadeMinimaItens;

    @Setter
    @Column(precision = 15, scale = 2)
    private BigDecimal valorMinimoPedido;

    @Setter
    @Column(nullable = false)
    private Integer limiteUsos;

    @OneToMany(mappedBy = "cupomDesconto")
    private Set<PedidoCupom> pedidos = new HashSet<>();

    public CupomDesconto(String codigo, BigDecimal valorDesconto, TipoDesconto tipoDesconto, LocalDate dataValidade, Integer quantidadeMinimaItens, BigDecimal valorMinimoPedido, Integer limiteUsos) {
        this.codigo = codigo;
        this.valorDesconto = valorDesconto;
        this.tipoDesconto = tipoDesconto;
        this.dataValidade = dataValidade;
        this.quantidadeMinimaItens = quantidadeMinimaItens;
        this.valorMinimoPedido = valorMinimoPedido;
        this.limiteUsos = limiteUsos;
    }


    // HELPERS
    public void addPedidoCupom(PedidoCupom pedidoCupom){
        pedidos.add(pedidoCupom);
    }

    //TODO - AJUSTAR PARA QUERY COUNT NO BANCO
    public boolean atingiuLimiteUsos(){
        return limiteUsos != null && pedidos.size() >= limiteUsos;
    }



}
