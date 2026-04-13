package com.finalphase.fabricapins.ecommerce.dto.produto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO de requisição do Produto")
public record ProdutoRequest(

        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 150, message = "Nome do Produto precisa estar entre 3 e 150 caracteres")
        @Schema(description = "Nome do Produto", example = "Pin Girassol")
        String nome,

        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 3000, message = "Descrição precisa estar entre 3 e 3000 caracteres")
        @Schema(description = "Descrição do Produto", example = "Pin dourado metálico 30x13x2mm com acabamento esmaltado")
        String descricao,

        @Schema(description = "URL da imagem do produto", example = "images/produto-123.jpg")
        String imgUrl,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Peso do produto (g)", example = "13")
        Double peso,

        @Positive
        @Schema(description = "Altura do produto (mm)", example = "10")
        Double altura,

        @Positive
        @Schema(description = "Largura do produto (mm)", example = "20")
        Double largura,

        @Positive
        @Schema(description = "Comprimento do produto (mm)", example = "30")
        Double comprimento,

        @Schema(description = "Indica se o produto será exibido como destaque na loja", example = "true")
        boolean destaque,

        @Schema(description = "Produto ativo", example = "true")
        boolean ativo,

        @NotNull
        @Schema(description = "ID da categoria do produto", example = "2")
        Long categoriaId
) {}