package com.finalphase.fabricapins.dto.categoria;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO de requisição do Cliente")
public record CategoriaRequest(
        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 100, message = "Nome da Categoria precisa estar entre 3 e 100 caracteres")
        @Schema(description = "Nome do Categoria", example = "Pin")
        String nome,

        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 250, message = "Nome do Cliente precisa estar entre 3 e 250 caracteres")
        @Schema(description = "Descrição da Categoria", example = "Pins colecionáveis")
        String descricao,

        @Schema(description = "Cliente ativo", example = "true")
        boolean ativa
) {}
