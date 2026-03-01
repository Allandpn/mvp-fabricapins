package com.finalphase.fabricapins.dto.perfil;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de resposta do Perfil com id e nome")
public record PerfilMinDTO (

        @Schema(description = "Id do perfil", example = "1")
        Long id,
        @Schema(description = "Nome do perfil", example = "ROLE_ADMIN")
        String nome
) {}