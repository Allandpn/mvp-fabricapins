package com.finalphase.fabricapins.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "DTO de requisição do Usuario")
public record UsuarioRequest(
        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 30, message = "Nome do Perfil precisa estar entre 3 e 30 caracteres")
        @Schema(description = "Nome do Usuario", example = "Maria123")
        String username,

        @NotBlank(message = "Campo requerido")
        @Size(min = 8, max = 100, message = "Senha precisa estar entre 8 e 100 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
                message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula e um número"
        )
        String password,

        boolean ativo,

        @NotEmpty(message = "O usuário deve possuir ao menos um perfil")
        List<@NotBlank String> perfis
) {}
