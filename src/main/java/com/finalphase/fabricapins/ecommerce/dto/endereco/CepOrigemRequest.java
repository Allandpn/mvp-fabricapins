package com.finalphase.fabricapins.ecommerce.dto.endereco;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para alteração do CEP de origem")
public record CepOrigemRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Cep de origem", example = "88600000")
        String cep
) {}
