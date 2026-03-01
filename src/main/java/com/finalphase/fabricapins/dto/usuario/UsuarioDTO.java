package com.finalphase.fabricapins.dto.usuario;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.finalphase.fabricapins.dto.perfil.PerfilMinDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Schema(description = "DTO de resposta do Perfil com id e nome")
public record UsuarioDTO(

        @Schema(description = "Id do perfil", example = "1")
        Long id,

        @Schema(description = "Nome do perfil", example = "Maria123")
        String username,

        @Schema(description = "Situação do Usuario", example = "true")
        boolean ativo,

        @Schema(description = "Data da criação do Usuario", example = "01/01/2026")
        Instant dataCriacao,

        @Schema(description = "Lista de Perfis do Usuario", example = "[{'id': '1', 'nome': 'ADMIN'},{'id': '2', 'nome': 'CLIENTE'}]")
        List<PerfilMinDTO> perfis
) {}
