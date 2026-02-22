package com.finalphase.fabricapins.dto.usuario;

import java.time.Instant;

public record UsuarioRequest(
        String username,
        String password,
        boolean ativo,
        Long clienteId
) {}
