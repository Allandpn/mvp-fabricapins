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
            example = " ")
    private Instant timestamp;

    @Schema(description = "Código HTTP do erro",
            example = " ")
    private Integer status;

    @Schema(description = "Descrição do erro",
            example = " ")
    private String error;

    @Schema(description = "Mensagem detalhada",
            example = " ")
    private String message;

    @Schema(description = "Caminho da requisição",
            example = " ")
    private String path;
}
