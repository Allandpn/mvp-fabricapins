package com.finalphase.fabricapins.exception.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Schema(description = "Estrutura padrão de erro da API")
public class CustomError {

    @Schema(description = "Momento em que o erro ocorreu",
            example = "2026-02-28T22:45:10Z")
    private Instant timestamp;

    @Schema(description = "Código HTTP do erro",
            example = "404")
    private Integer status;

    @Schema(description = "Descrição do erro",
            example = "Recurso não encontrado")
    private String error;

    @Schema(description = "Mensagem detalhada",
            example = "Perfil com id 5 não encontrado")
    private String message;

    @Schema(description = "Caminho da requisição",
            example = "/perfil/5")
    private String path;
}
