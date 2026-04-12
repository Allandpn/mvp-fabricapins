package com.finalphase.fabricapins.ecommerce.dto.cliente;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoPessoa;

import java.time.Instant;

public record ClienteMinDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        TipoPessoa tipoPessoa,
        String numeroDocumento,
        TipoCliente tipoCliente,
        Instant dataCadastro,
        Instant dataAtualizacao,
        boolean ativo
) {}

