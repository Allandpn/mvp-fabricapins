package com.finalphase.fabricapins.ecommerce.dto.cupom_desconto;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoDesconto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "DTO de requisição do Cupom Desconto")
public record CupomDescontoRequest(
        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 30, message = "Nome da Cupom precisa estar entre 3 e 30 caracteres")
        @Schema(description = "Nome do Cupom", example = "GANHOU10")
        String codigo,

        @Schema(description = "Cupom ativo", example = "true")
        boolean ativo,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Valor do Desconto", example = "10.00")
        BigDecimal valorDesconto,

        @NotNull
        @Schema(description = "Tipo de Desconto", example = "PERCENTUAL")
        TipoDesconto tipoDesconto,

        @FutureOrPresent
        @Schema(description = "Data de validade do Cupom", example = "2026-01-30")
        LocalDate dataValidade,

        @Schema(description = "Quantidade minima de items", example = "10")
        Integer quantidadeMinimaItens,

        @Schema(description = "Valor mínimo do pedido", example = "100.00")
        BigDecimal valorMinimoPedido,

        @Schema(description = "Limite de Usos", example = "10")
        Integer limiteUsos
) {
}
