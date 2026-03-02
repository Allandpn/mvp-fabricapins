package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private BigDecimal desconto;

    @Setter
    @Column(nullable = false, unique = true, length = 50)
    private String numeroPedido;

    @Setter
    @Column(precision = 15, scale = 2)
    private BigDecimal valorFrete;

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
    private String cpfCnpjClienteSnapshot;

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
    private Integer numero;
    @Setter
    private String complemento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<ItemPedido> itemsPedido = new ArrayList<>();

    @OneToMany(mappedBy = "id.pedido")
    private Set<PedidoCupom> pedidoCupomSet = new HashSet<>();

    public Pedido(Cliente cliente, String numeroPedido) {
        this.cliente = cliente;
        this.numeroPedido = numeroPedido;
        this.statusPedido = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.valorTotal = BigDecimal.ZERO;
        this.valorSubtotal = BigDecimal.ZERO;
    }

    // HELPERS
    public void adicionarItem(ItemPedido item){
        itemsPedido.add(item);
        item.setPedido(this);
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
        BigDecimal desc = desconto != null ? desconto : BigDecimal.ZERO;
        this.valorTotal = subTotal
                .subtract(desc)
                .add(valorFrete != null ? valorFrete : BigDecimal.ZERO);
        this.valorSubtotal = subTotal;
    }
}
