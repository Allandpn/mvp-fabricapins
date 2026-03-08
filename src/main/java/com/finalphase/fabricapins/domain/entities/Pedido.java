package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.domain.enums.TipoDesconto;
import com.finalphase.fabricapins.exception.BusinessException;
import com.finalphase.fabricapins.exception.DateOutOfBoundsException;
import com.finalphase.fabricapins.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tb_pedido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pedido {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dataCriacao;

    @UpdateTimestamp
    private Instant dataAtualizacao;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido statusPedido;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrigemPedido origemPedido;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Setter
    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Setter
    @Column(precision = 15, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Setter
    @Column(nullable = false, unique = true, length = 50)
    private String codigoPedido;

    @Setter
    @Column(precision = 15, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Setter
    private LocalDate dataPrevistaProducao;

    @Setter
    private LocalDate dataConclusaoPedido;

    @Setter
    private LocalDate dataEnvio;

    @Setter
    private LocalDate dataEntrega;

    // Dados do cliente snapshot
    @Setter
    @Column(nullable = false)
    private String nomeClienteSnapshot;
    @Setter
    @Column(nullable = false)
    private String documentoClienteSnapshot;

    // Endereco snapshot
    @Setter
    @Column(nullable = false)
    private String cep;
    @Setter
    @Column(nullable = false)
    private String estado;
    @Setter
    @Column(nullable = false)
    private String cidade;
    @Setter
    @Column(nullable = false)
    private String bairro;
    @Setter
    @Column(nullable = false)
    private String logradouro;
    @Setter
    @Column(nullable = false)
    private String numero;
    @Setter
    private String complemento;
    @Setter
    private String pontoReferencia;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Setter
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<ItemPedido> itemsPedido = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PedidoCupom> cupons = new HashSet<>();

    public Pedido(Cliente cliente, String codigoPedido, String nomeClienteSnapshot, String documentoClienteSnapshot) {
        this.cliente = cliente;
        this.codigoPedido = codigoPedido;
        this.nomeClienteSnapshot = nomeClienteSnapshot;
        this.documentoClienteSnapshot = documentoClienteSnapshot;
        this.statusPedido = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.valorTotal = BigDecimal.ZERO;
        this.valorSubtotal = BigDecimal.ZERO;
    }

    // HELPERS
    public void adicionarItem(ItemPedido item){
        ProdutoVariacao produtoVariacao = item.getProdutoVariacao();
        if(produtoVariacao.getQuantidadeEstoque() < item.getQuantidade()){
            throw new InsufficientStockException(
                    "Estoque insuficiente para o produto: " + produtoVariacao.getNome() + "- id: " + produtoVariacao.getId());
        }
        produtoVariacao.reduzirEstoque(item.getQuantidade());
        item.setPedido(this);
        this.itemsPedido.add(item);
        recalcularTotal();
    }

    public void removerItem(ItemPedido item){
        itemsPedido.remove(item);
        item.setPedido(null);
        recalcularTotal();
    }

    private  void recalcularTotal(){
        BigDecimal subTotal = itemsPedido.stream()
                .map(ItemPedido::calcularSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorSubtotal = subTotal;
        BigDecimal descontoCupons = cupons.stream()
                .map(pc -> pc.recalcularValor(this))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal descontoFinal = descontoCupons.min(subTotal);
        this.desconto = descontoFinal;
        BigDecimal frete = valorFrete != null ? valorFrete : BigDecimal.ZERO;
        this.valorTotal = subTotal
                .subtract(descontoFinal)
                .add(frete)
                .max(BigDecimal.ZERO);
    }

    public void aplicarCupom(CupomDesconto cupom){
        validaSeCupomAtivo(cupom);
        validaCupomPercentualDuplicado(cupom);
        validaQuantidadeMinimaItens(cupom);
        validaValorMinimoPedido(cupom);
        validaCupomDuplicado(cupom);
        validaDataLimiteUso(cupom);
        validaLimiteUsos(cupom);
        PedidoCupom pedidoCupom = new PedidoCupom(this, cupom);
        recalcularTotal();
    }

    public void removerCupom(String codigoCupom){
        Iterator<PedidoCupom> iterator = cupons.iterator();

        while (iterator.hasNext()){
            PedidoCupom pedidoCupom = iterator.next();
            if(pedidoCupom.getCodigoCupom().equals(codigoCupom)){
                iterator.remove();
                pedidoCupom.desvincular();
            }
        }
        recalcularTotal();
    }

    public String gerarCodigoPedido() {
        return "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    //VALIDADORES

    private void validaCupomDuplicado(CupomDesconto cupom) {
        boolean match = cupons.stream()
                .anyMatch(pedidoCupom -> pedidoCupom.getCodigoCupom().equals(cupom.getCodigo()));
        if (match) {
            throw new BusinessException("Cupom já aplicado no pedido");
        }
    }

    private void validaCupomPercentualDuplicado(CupomDesconto cupom) {
        boolean percentualJaAplicado = cupons.stream()
                .anyMatch(pedidoCupom -> pedidoCupom.getTipoDesconto().equals(TipoDesconto.PERCENTUAL));
        if(cupom.getTipoDesconto() == TipoDesconto.PERCENTUAL && percentualJaAplicado){
            throw new BusinessException("Ja existe um cupom de desconto percentual aplicado");
        }
    }

    private void validaQuantidadeMinimaItens(CupomDesconto cupom) {
        Integer qntItems = itemsPedido.stream().map(ItemPedido::getQuantidade).reduce(0, Integer::sum);
        if(cupom.getQuantidadeMinimaItens() != null &&
            qntItems.compareTo(cupom.getQuantidadeMinimaItens()) < 0){
                throw new BusinessException(
                        "Pedido não atingiu a quantidade mínima de produdos [" + cupom.getQuantidadeMinimaItens() + "]");
        }
    }

    private void validaValorMinimoPedido(CupomDesconto cupom) {
        if(cupom.getValorMinimoPedido() != null &&
            valorSubtotal.compareTo(cupom.getValorMinimoPedido()) < 0) {
                throw new BusinessException("Pedido não atingiu o valor mínimo necessário [" + cupom.getValorMinimoPedido() + "]");
        }
    }

    private void validaLimiteUsos(CupomDesconto cupom) {
        if(cupom.atingiuLimiteUsos()){
            throw new BusinessException("Cupom esgotado");
        }
    }

    private void validaDataLimiteUso(CupomDesconto cupom) {
        if(cupom.getDataValidade() != null && cupom.getDataValidade().plusDays(1).atStartOfDay().isBefore(LocalDateTime.now())){;
            throw new DateOutOfBoundsException("Cupom expirado");
        }
    }

    private void validaSeCupomAtivo(CupomDesconto cupom) {
        if(!cupom.isAtivo()){;
            throw new DateOutOfBoundsException("Cupom não encontrado");
        }
    }
}


