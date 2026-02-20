package com.finalphase.fabricapins.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_usuario")
@Getter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String username;

    @Setter
    private String password;

    @ManyToMany(mappedBy = "usuarios")
    private Set<Role> roles = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
