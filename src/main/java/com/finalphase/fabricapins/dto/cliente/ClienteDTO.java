package com.finalphase.fabricapins.dto.cliente;

import com.finalphase.fabricapins.domain.enums.TipoCliente;

import java.time.Instant;

public record ClienteDTO(
        Long id,
        String nome,
        String cpf,
        String cnpj,
        String email,
        String telefone,
        TipoCliente tipoCliente,
        Instant dataCadastro,
        Instant dataAtualizacao,
        boolean ativo,
        Long usuarioId
) {}

