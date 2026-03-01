package com.finalphase.fabricapins.dto.perfil;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de requisição do Perfil")
public record PerfilRequest(

        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 30, message = "Nome do Perfil precisa estar entre 3 e 30 caracteres")
        @Schema(description = "Nome do Perfil", example = "ROLE_ADMIN")
        String nome
) {}
