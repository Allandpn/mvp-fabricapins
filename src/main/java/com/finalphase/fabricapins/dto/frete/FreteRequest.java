package com.finalphase.fabricapins.dto.frete;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "DTO de requisição da opcao de frete")
public record FreteRequest(
        @NotBlank(message = "Campo requerido")
        @Schema(description = "Id do item", example = "1")
        String serviceId
) {}
