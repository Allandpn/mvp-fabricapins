package com.finalphase.fabricapins.dto.cliente;

import com.finalphase.fabricapins.domain.enums.TipoCliente;

public record ClienteRequest(
        String nome,
        String cpf,
        String cnpj,
        String email,
        String telefone,
        TipoCliente tipoCliente,
        boolean ativo,
        Long usuarioId
) {}
