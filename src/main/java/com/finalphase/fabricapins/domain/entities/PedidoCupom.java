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
    @EqualsAndHashCode.Include
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

    @Column(nullable = false)
    private String codigoCupom;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoDesconto tipoDesconto;

    public PedidoCupom(Pedido pedido, CupomDesconto cupom){
        this.pedido = pedido;
        this.cupomDesconto = cupom;
        this.valorDescontoAplicado = calculaDescontoCupom(pedido, cupom);
        this.codigoCupom = cupom.getCodigo();
        this.tipoDesconto = cupom.getTipoDesconto();
        pedido.getCupons().add(this);
        cupom.getPedidos().add(this);
    }



    // HELPERS
    private BigDecimal calculaDescontoCupom(Pedido pedido,CupomDesconto cupom){
        if(cupom.getTipoDesconto() == TipoDesconto.PERCENTUAL){
            return pedido.getValorSubtotal()
                    .multiply(cupom.getValorDesconto())
                    .divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
        }
        return cupom.getValorDesconto();
    }

    public BigDecimal recalcularValor(Pedido pedido){
        return calculaDescontoCupom(pedido, this.cupomDesconto);
    }

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
