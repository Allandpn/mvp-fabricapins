package com.finalphase.fabricapins.dto.cliente;

import com.finalphase.fabricapins.domain.entities.Cliente;

public record ClienteSnapshot(
        Cliente cliente,
        String nomeCliente,
        String numeroDocumento
) {}
