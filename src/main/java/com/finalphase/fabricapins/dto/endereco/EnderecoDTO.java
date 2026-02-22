package com.finalphase.fabricapins.dto.endereco;

import com.finalphase.fabricapins.domain.enums.TipoEndereco;

import java.time.Instant;

public record EnderecoDTO(
        Long id,
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
        Instant dataCadastro,
        String apelido,
        Long clienteId
) {}
