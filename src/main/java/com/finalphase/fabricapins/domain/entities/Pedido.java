package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.dto.cliente.ClienteSnapshot;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    private Instant dataConclusaoPedido;

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
    private Instant dataCalculoFrete;

    @Setter
    @Column(precision = 15, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Setter
    private String nomeServicoFrete;

    @Setter
    private Integer prazoEntregaDias;

    @Setter
    private String freteEmpresa;

    @Setter
    private Instant dataEnvio;

    @Setter
    private Instant dataEntrega;

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

    // Dados para KPIs
    private Instant dataPagamentoConfirmado;
    private Instant dataInicioProducao;
    private Instant dataFimProducao;
    private Instant dataSeparacao;
    private Instant dataAguardandoEnvio;
    private Instant dataCancelamento;

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

    @Setter
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpcaoFretePedido> opcoesFrete = new ArrayList<>();

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
        item.setPedido(this);
        this.itemsPedido.add(item);
        invalidarFrete();
        recalcularTotal();
    }

    public void incrementarItem(ItemPedido item, Integer quantidade){
        if(quantidade <= 0){
            throw new BusinessException("Quantidade inválida");
        }
        item.setQuantidade(item.getQuantidade() + quantidade);
        invalidarFrete();
        recalcularTotal();
    }

    public void atualizarQuantidade(ItemPedido item, Integer quantidade){
        if(quantidade <= 0){
            throw new BusinessException("Quantidade inválida");
        }
        item.setQuantidade(quantidade);
        invalidarFrete();
        recalcularTotal();
    }


    public void removerItem(ItemPedido item){
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
        pedidoCupom.setPedido(this);
        recalcularTotal();
    }

    public void removerCupom(String codigoCupom){
        cupons.removeIf(c -> {
            if(Objects.equals(c.getCodigoCupom(), codigoCupom)){
                c.desvincular();
                return true;
            }
            return false;
        });
        recalcularTotal();
    }

    public String gerarCodigoPedido() {
        String dataHora = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
                .format(DateTimeFormatter.ofPattern("MMddHHmmssSSS"));
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return dataHora + "-" + random;
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

    public void definirFrete(OpcaoFretePedido opcao){
        this.freteServiceId = opcao.getServiceId();
        this.valorFrete = opcao.getValor();
        this.nomeServicoFrete = opcao.getNome();
        this.prazoEntregaDias = opcao.getPrazoDias();
        this.freteEmpresa = opcao.getEmpresa();
        this.dataCalculoFrete = Instant.now();
        recalcularTotal();
    }

    public void invalidarFrete(){
        this.freteServiceId = null;
        this.valorFrete = BigDecimal.ZERO;
        this.nomeServicoFrete = null;
        this.prazoEntregaDias = null;
        this.freteEmpresa = null;
        this.dataCalculoFrete = null;
        this.opcoesFrete.clear();
    }

    public void confirmar(){
        validarConfirmacao();
        this.statusPedido = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.dataConclusaoPedido = Instant.now();
    }
    
    public void cancelar(){
        if(this.statusPedido == StatusPedido.CANCELADO){
            throw new BusinessException("Pedido ja está cancelado");
        }
        if(this.statusPedido  != StatusPedido.RASCUNHO &&
                this.statusPedido  != StatusPedido.AGUARDANDO_PAGAMENTO &&
                this.statusPedido  != StatusPedido.PAGAMENTO_CONFIRMADO &&
                this.statusPedido  != StatusPedido.EM_PRODUCAO &&
                this.statusPedido  != StatusPedido.EM_SEPARACAO &&
                this.statusPedido  != StatusPedido.AGUARDANDO_ENVIO){
            throw new BusinessException("O pedido não pode ser cancelado.");
        }
        this.statusPedido = StatusPedido.CANCELADO;
        this.dataCancelamento = Instant.now();
    }

    private void validarConfirmacao() {
        if(this.statusPedido != StatusPedido.RASCUNHO){
            throw new BusinessException("Apenas pedidos em rascunho podem ser confirmados");
        }
        if(this.itemsPedido.isEmpty()){
            throw new BusinessException("Pedido deve possuir itens");
        }
        if(this.nomeCliente == null){
            throw new BusinessException("Cliente não informado");
        }
        if(this.cep == null){
            throw new BusinessException("Endereço não informado");
        }
        if(this.freteServiceId == null){
            throw new BusinessException("Frete não selecionado");
        }
        if(this.valorTotal.compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessException("Valor total inválido");
        }
    }

    public void confirmarPagamento(){
        if(this.statusPedido != StatusPedido.AGUARDANDO_PAGAMENTO){
            throw new BusinessException("Pedido não possui pagamento pendente");
        }
        this.statusPedido = StatusPedido.PAGAMENTO_CONFIRMADO;
        this.dataPagamentoConfirmado = Instant.now();
    }

    public void iniciarProducao(){
        if(this.statusPedido != StatusPedido.PAGAMENTO_CONFIRMADO){
            throw new BusinessException("Pedido não pode entrar em produção");
        }
        this.statusPedido = StatusPedido.EM_PRODUCAO;
        this.dataInicioProducao = Instant.now();
    }

    public void iniciarSeparacao(){
        if(this.statusPedido != StatusPedido.EM_PRODUCAO){
            throw new BusinessException("Pedido não está em produção");
        }
        this.statusPedido = StatusPedido.EM_SEPARACAO;
        this.dataFimProducao = Instant.now();
        this.dataSeparacao = Instant.now();
    }

    public void aguardarEnvio(){
        if(this.statusPedido != StatusPedido.EM_SEPARACAO){
            throw new BusinessException("Pedido não está em separação");
        }
        this.statusPedido = StatusPedido.AGUARDANDO_ENVIO;
        this.dataAguardandoEnvio = Instant.now();
    }

    public void enviar(){
        if(this.statusPedido != StatusPedido.AGUARDANDO_ENVIO){
            throw new BusinessException("Pedido não está pronto para envio");
        }

        this.statusPedido = StatusPedido.ENVIADO;
        this.dataEnvio = Instant.now();
    }

    public void entregar(){
        if(this.statusPedido != StatusPedido.ENVIADO){
            throw new BusinessException("Pedido não foi enviado");
        }
        this.statusPedido = StatusPedido.ENTREGUE;
        this.dataEntrega = Instant.now();
    }

    public Duration tempoProducao(){
        if(dataInicioProducao == null || dataFimProducao == null) return null;
        return Duration.between(dataInicioProducao, dataFimProducao);
    }

    public Duration tempoTotalPedido(){
        if(dataEntrega == null) return null;
        return Duration.between(dataCriacao, dataEntrega);
    }

    public void incluirFreteAdmin(BigDecimal valorFrete){
        this.valorFrete = valorFrete;
        recalcularTotal();
    }

}


