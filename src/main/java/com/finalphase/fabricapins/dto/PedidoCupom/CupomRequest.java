package com.finalphase.fabricapins.dto.PedidoCupom;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para inserção de cupons de desconto")
public record CupomRequest(
        @NotNull(message = "Campo requerido")
        @Schema(description = "Cupom de Desconto", example = "DESCONTO10")
        String codigo
) {}
