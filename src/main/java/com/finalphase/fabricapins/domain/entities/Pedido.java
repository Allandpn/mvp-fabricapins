package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.dto.cliente.ClienteSnapshot;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoRequest;
import com.finalphase.fabricapins.dto.frete.OpcaoFreteDTO;
import com.finalphase.fabricapins.exception.InsufficientStockException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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
    private String freteServiceId;

    @Setter
    @Column(precision = 15, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Setter
    private String nomeServicoFrete;

    @Setter
    private Integer prazoEntregaDias;

    @Setter
    private LocalDate dataPrevistaProducao;

    @Setter
    private LocalDate dataConclusaoPedido;

    @Setter
    private LocalDate dataEnvio;

    @Setter
    private LocalDate dataEntrega;

    @Setter
    private String observacao;

    // Dados do cliente snapshot
    @Setter
    private String nomeCliente;
    @Setter
    private String documentoCliente;
    @Setter
    private String telefone;
    @Setter
    @Enumerated(EnumType.STRING)
    private TipoCliente tipoCliente;

    // Endereco snapshot
    @Setter
    private String cep;
    @Setter
    private String estado;
    @Setter
    private String cidade;
    @Setter
    private String bairro;
    @Setter
    private String logradouro;
    @Setter
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

    // TODO - evoluir para cache/redis
    @Setter
    @Transient
    private List<OpcaoFreteDTO> opcoesFreteCalculadas;

    public Pedido(ClienteSnapshot cliente) {
        this.cliente = cliente.cliente();
        this.nomeCliente = cliente.nome();
        this.documentoCliente = cliente.numeroDocumento();
        this.telefone = cliente.telefone();
        this.tipoCliente = cliente.tipoCliente();
        this.statusPedido = StatusPedido.RASCUNHO;
        this.valorTotal = BigDecimal.ZERO;
        this.valorSubtotal = BigDecimal.ZERO;
        this.desconto = BigDecimal.ZERO;
        this.valorFrete = BigDecimal.ZERO;
    }

    // HELPERS
    public void adicionarItem(ItemPedido item){
        ProdutoVariacao produtoVariacao = item.getProdutoVariacao();
        if(produtoVariacao.getQuantidadeEstoque().compareTo(item.getQuantidade()) < 0){
            throw new InsufficientStockException(
                    "Estoque insuficiente para o produto: " + produtoVariacao.getNome() + "- id: " + produtoVariacao.getId());
        }
        produtoVariacao.reduzirEstoque(item.getQuantidade());
        item.setPedido(this);
        this.itemsPedido.add(item);
        invalidarFrete();
        recalcularTotal();
    }

    public void incrementarItem(ItemPedido item, Integer quantidade){
        ProdutoVariacao produto = item.getProdutoVariacao();
        if(produto.getQuantidadeEstoque().compareTo(quantidade) < 0){
            throw new InsufficientStockException(
                    "Estoque insuficiente para o produto: " + produto.getNome() + "- id: " + produto.getId());
        }
        produto.reduzirEstoque(quantidade);
        item.setQuantidade(item.getQuantidade() + quantidade);
        invalidarFrete();
        recalcularTotal();
    }


    public void removerItem(ItemPedido item){
        ProdutoVariacao produto = item.getProdutoVariacao();
        produto.aumentarEstoque(item.getQuantidade());
        itemsPedido.remove(item);
        item.setPedido(null);
        invalidarFrete();
        recalcularTotal();
    }

    public void recalcularTotal(){
        BigDecimal subTotal = itemsPedido.stream()
                .map(ItemPedido::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.valorSubtotal = subTotal;
        BigDecimal descontoCupons = cupons.stream()
                .map(PedidoCupom::getValorDescontoAplicado)
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
        cupom.validarAplicacaoCupom(this);
        PedidoCupom pedidoCupom = new PedidoCupom(this, cupom);
        this.cupons.add(pedidoCupom);
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


    public void definirEndereco(EnderecoPedidoDTO dto){
            this.cep = dto.cep();
            this.estado = dto.estado();
            this.cidade = dto.cidade();
            this.bairro = dto.bairro();
            this.logradouro = dto.logradouro();
            this.numero = dto.numero();
            this.complemento = dto.complemento();
            this.pontoReferencia = dto.pontoReferencia();
            invalidarFrete();
    }

    public void definirFrete(OpcaoFreteDTO opcao){
        this.freteServiceId = opcao.serviceId();
        this.valorFrete = opcao.valor();
        this.nomeServicoFrete = opcao.nome();
        this.prazoEntregaDias = opcao.prazoDias();
        recalcularTotal();
    }

    public void invalidarFrete(){
        this.freteServiceId = null;
        this.valorFrete = BigDecimal.ZERO;
        this.nomeServicoFrete = null;
        this.prazoEntregaDias = null;
        this.opcoesFreteCalculadas = null;
    }



}


