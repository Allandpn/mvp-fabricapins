package com.finalphase.fabricapins.dto.endereco;

import com.finalphase.fabricapins.domain.entities.Endereco;

public record EnderecoPedidoDTO(
        String cep,
        String estado,
        String cidade,
        String bairro,
        String logradouro,
        String numero,
        String complemento,
        String pontoReferencia
    ) {}
