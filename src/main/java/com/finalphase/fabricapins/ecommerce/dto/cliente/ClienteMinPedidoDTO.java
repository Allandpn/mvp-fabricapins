package com.finalphase.fabricapins.ecommerce.dto.cliente;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;

public record ClienteMinPedidoDTO(
        Long id,
        String nome,
        String numeroDocumento,
        String telefone,
        TipoCliente tipoCliente
) {}

