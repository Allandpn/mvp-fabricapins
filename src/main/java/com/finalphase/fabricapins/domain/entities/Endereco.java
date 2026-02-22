package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.TipoEndereco;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_endereco")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Endereco {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank
    @Column(nullable = false, length = 8)
    private String cep;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String estado;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String cidade;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String bairro;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String logradouro;

    @Setter
    @NotBlank
    @Column(nullable = false)
    private String numero;

    @Setter
    private String complemento;

    @Setter
    private String pontoReferencia;

    @Setter
    private String observacoes;

    @Setter
    @Column(nullable = false)
    private boolean enderecoPrincipal = false;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoEndereco tipoEndereco;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dataCadastro;

    @Setter
    private String apelido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Endereco(String cep, String estado, String cidade, String bairro, String logradouro, String numero, String complemento, String pontoReferencia, String observacoes, TipoEndereco tipoEndereco, Instant dataCadastro, String apelido, Cliente cliente) {
        this.cep = cep;
        this.estado = estado;
        this.cidade = cidade;
        this.bairro = bairro;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.pontoReferencia = pontoReferencia;
        this.observacoes = observacoes;
        this.tipoEndereco = tipoEndereco;
        this.dataCadastro = dataCadastro;
        this.apelido = apelido;
        this.cliente = cliente;
    }
}
