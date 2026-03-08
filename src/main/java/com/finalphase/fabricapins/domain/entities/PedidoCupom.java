package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_pedido_cupom")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PedidoCupom {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private PedidoCupomPK id = new PedidoCupomPK();

    private LocalDate dataAplicacao;

    private BigDecimal valorDescontoAplicado;


    // HELPERS
    public void adicionarPedido(Pedido pedido){
        this.id.setPedido(pedido);
    }

    public void removerPedido(){
        this.id.setPedido(null);
    }
}
