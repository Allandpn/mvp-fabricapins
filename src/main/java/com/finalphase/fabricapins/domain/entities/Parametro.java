package com.finalphase.fabricapins.domain.entities;

import com.finalphase.fabricapins.domain.enums.ParametroChave;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_parametro")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Parametro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ParametroChave chave;

    @Column(nullable = false)
    private String valor;

    public Parametro(ParametroChave chave, String valor) {
        this.chave = chave;
        this.valor = valor;
    }

    public void atualizarValor(String valor){
        this.valor = valor;
    }
}
