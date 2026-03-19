package com.finalphase.fabricapins.dto.cliente;

import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.domain.enums.TipoPessoa;

import java.time.Instant;

public record ClienteMinPedidoDTO(
        Long id,
        String nome,
        String numeroDocumento,
        String telefone,
        TipoCliente tipoCliente
) {}

