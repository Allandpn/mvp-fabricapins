package com.finalphase.fabricapins.dto.perfil;

import com.finalphase.fabricapins.dto.usuario.UsuarioMinForPerfilDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "DTO de resposta do Perfil com id e nome e usuarios")
public record PerfilWithUsuariosDTO(

        @Schema(description = "Id do perfil", example = "1")
        Long id,
        @Schema(description = "Nome do perfil", example = "ROLE_ADMIN")
        String nome,
        @Schema(description = "Usuario desse perfil", example = "{'id': 1, 'nome': 'Mateus'}")
        Set<UsuarioMinForPerfilDTO> usuarios
) {
}
