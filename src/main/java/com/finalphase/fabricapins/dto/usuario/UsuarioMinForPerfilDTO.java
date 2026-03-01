package com.finalphase.fabricapins.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta do Usuario com id e nome")
public record UsuarioMinForPerfilDTO(

        @Schema(description = "Id do usuario", example = "1")
        Long id,
        @Schema(description = "Nome do usuario", example = "Mateus")
        String username
) {}
