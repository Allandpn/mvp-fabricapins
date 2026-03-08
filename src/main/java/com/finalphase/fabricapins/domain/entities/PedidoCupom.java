package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.TipoDesconto;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Entity
@Table(name = "tb_pedido_cupom")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PedidoCupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_id")
    private CupomDesconto cupomDesconto;

    @CreationTimestamp
    private Instant dataAplicacao;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDescontoAplicado;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String codigoCupom;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDesconto tipoDesconto;

    public PedidoCupom(Pedido pedido, CupomDesconto cupom){
        this.pedido = pedido;
        this.cupomDesconto = cupom;
        this.valorDescontoAplicado = cupom.calculaDesconto(pedido);
        this.codigoCupom = cupom.getCodigo();
        this.tipoDesconto = cupom.getTipoDesconto();
    }



    // HELPERS

    public void desvincular(){
        if(pedido != null){
            pedido.getCupons().remove(this);
        }
        if(cupomDesconto != null){
            cupomDesconto.getPedidos().remove(this);
        }
        this.pedido = null;
    }


}
