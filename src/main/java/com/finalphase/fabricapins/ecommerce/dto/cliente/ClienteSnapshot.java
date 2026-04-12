package com.finalphase.fabricapins.ecommerce.dto.cliente;

import com.finalphase.fabricapins.ecommerce.domain.entities.Cliente;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;


public record ClienteSnapshot(
        Cliente cliente,
        String nome,
        String numeroDocumento,
        String telefone,
        TipoCliente tipoCliente
) {}
