package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.TipoDesconto;
import com.finalphase.fabricapins.exception.BusinessException;
import com.finalphase.fabricapins.exception.DateOutOfBoundsException;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public BigDecimal calculaDesconto(Pedido pedido){
        if(this.getTipoDesconto() == TipoDesconto.PERCENTUAL){
            return pedido.getValorSubtotal()
                    .multiply(this.getValorDesconto())
                    .divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP);
        }
        return this.getValorDesconto();
    }


    //TODO - AJUSTAR PARA QUERY COUNT NO BANCO
    public boolean atingiuLimiteUsos(){
        return limiteUsos != null && pedidos.size() >= limiteUsos;
    }

    public void validarAplicacaoCupom(Pedido pedido){
        validaSeCupomAtivo();
        validaCupomPercentualDuplicado(pedido);
        validaQuantidadeMinimaItens(pedido);
        validaValorMinimoPedido(pedido);
        validaCupomDuplicado(pedido);
        validaDataLimiteUso();
    }




    //VALIDADORES

    private void validaCupomDuplicado(Pedido pedido) {
        boolean match = pedido.getCupons().stream()
                .anyMatch(pedidoCupom -> pedidoCupom.getCodigoCupom().equals(this.getCodigo()));
        if (match) {
            throw new BusinessException("Cupom já aplicado no pedido");
        }
    }

    private void validaCupomPercentualDuplicado(Pedido pedido) {
        boolean percentualJaAplicado = pedido.getCupons().stream()
                .anyMatch(pedidoCupom -> pedidoCupom.getTipoDesconto().equals(TipoDesconto.PERCENTUAL));
        if(this.getTipoDesconto() == TipoDesconto.PERCENTUAL && percentualJaAplicado){
            throw new BusinessException("Ja existe um this de desconto percentual aplicado");
        }
    }

    private void validaQuantidadeMinimaItens(Pedido pedido) {
        Integer qntItems = pedido.getItemsPedido().stream().map(ItemPedido::getQuantidade).reduce(0, Integer::sum);
        if(this.getQuantidadeMinimaItens() != null &&
                qntItems.compareTo(this.getQuantidadeMinimaItens()) < 0){
            throw new BusinessException(
                    "Pedido não atingiu a quantidade mínima de produdos [" + this.getQuantidadeMinimaItens() + "]");
        }
    }

    private void validaValorMinimoPedido(Pedido pedido) {
        if(this.getValorMinimoPedido() != null &&
                pedido.getValorSubtotal().compareTo(this.getValorMinimoPedido()) < 0) {
            throw new BusinessException("Pedido não atingiu o valor mínimo necessário [" + this.getValorMinimoPedido() + "]");
        }
    }

    private void validaDataLimiteUso() {
        if(this.getDataValidade() != null && this.getDataValidade().plusDays(1).atStartOfDay().isBefore(LocalDateTime.now())){;
            throw new DateOutOfBoundsException("Cupom expirado");
        }
    }

    private void validaSeCupomAtivo() {
        if(!this.isAtivo()){;
            throw new DateOutOfBoundsException("Cupom não encontrado");
        }
    }


}
