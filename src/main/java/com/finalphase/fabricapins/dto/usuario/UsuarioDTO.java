package com.finalphase.fabricapins.dto.usuario;

import java.time.Instant;

public record UsuarioDTO(
        Long id,
        String username,
        String password,
        boolean ativo,
        Instant dataCriacao,
        Long clienteId
) {}
