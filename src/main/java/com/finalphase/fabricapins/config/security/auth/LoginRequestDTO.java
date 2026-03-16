package com.finalphase.fabricapins.config.security.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 30, message = "Nome do Perfil precisa estar entre 3 e 30 caracteres")
        String username,

        @NotBlank(message = "Campo requerido")
        @Size(min = 8, max = 100, message = "Senha precisa estar entre 8 e 100 caracteres")
        String password
) {}
