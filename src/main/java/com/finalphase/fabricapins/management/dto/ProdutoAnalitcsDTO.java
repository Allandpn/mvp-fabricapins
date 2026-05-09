package com.finalphase.fabricapins.management.dto;

import com.finalphase.fabricapins.ecommerce.domain.enums.SituacaoEstoque;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoAnalitcsDTO {
    public String nome;
    public String categoriaNome;
    public Integer quantidadeEstoque;
    public Integer estoqueMinimo;
    public Integer vendidosPeriodo;
    public SituacaoEstoque situacao;
}