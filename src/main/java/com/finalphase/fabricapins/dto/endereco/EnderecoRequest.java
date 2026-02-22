package com.finalphase.fabricapins.dto.endereco;

import com.finalphase.fabricapins.domain.enums.TipoEndereco;

public record EnderecoRequest(
        String cep,
        String estado,
        String cidade,
        String bairro,
        String logradouro,
        String numero,
        String complemento,
        String pontoReferencia,
        String observacoes,
        boolean enderecoPrincipal,
        TipoEndereco tipoEndereco,
        String apelido,
        Long clienteId
) {}
