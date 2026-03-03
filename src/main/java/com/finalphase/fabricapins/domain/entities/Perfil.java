package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_perfil")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Perfil {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String GERENTE = "ROLE_GERENTE";
    public static final String VENDEDOR = "ROLE_VENDEDOR";
    public static final String CLIENTE = "ROLE_CLIENTE";


    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @ManyToMany(mappedBy = "perfis", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>();

    public Perfil(String nome) {
        this.nome = nome;
    }

}
