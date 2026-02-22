package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.TipoCliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_cliente")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cliente {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank
    @Column(nullable = false, length = 150)
    private String nome;

    @Setter
    @Column(length = 11, unique = true, nullable = false)
    @CPF
    private String cpf;

    @Setter
    @CNPJ
    private String cnpj;

    @Setter
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    private String telefone;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoCliente tipoCliente;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dataCadastro;

    @UpdateTimestamp
    private Instant dataAtualizacao;

    @Setter
    @Column(nullable = false)
    private boolean ativo = true;

    @OneToOne(mappedBy = "cliente")
    private Usuario usuario;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    @BatchSize(size = 20)
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 5)
    private List<Endereco> enderecos = new ArrayList<>();

    public Cliente(String nome, String email, String cpf, TipoCliente tipoCliente) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.tipoCliente = tipoCliente;
    }
}
