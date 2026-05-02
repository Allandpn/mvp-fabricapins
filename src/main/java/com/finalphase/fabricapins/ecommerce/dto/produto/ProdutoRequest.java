package com.finalphase.fabricapins.ecommerce.dto.produto;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoEstoqueProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "DTO de requisição do Produto")
public record ProdutoRequest(

        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 150, message = "Nome do Produto precisa estar entre 3 e 150 caracteres")
        @Schema(description = "Nome do Produto", example = "Pin Girassol")
        String nome,

        @NotBlank(message = "Campo requerido")
        @Size(max = 3000, message = "Descrição deve ter no máximo 3000 caracteres")
        @Schema(description = "Descrição do Produto", example = "Pin dourado metálico 30x13x2mm com acabamento esmaltado")
        String descricao,

        @NotNull(message = "Tipo de estoque é obrigatório")
        @Schema(description = "Tipo de estoque", example = "ESTOQUE")
        TipoEstoqueProduto tipoEstoque,

        @NotNull
        @PositiveOrZero(message = "Quantidade em estoque não pode ser negativa")
        @Schema(description = "Quantidade em estoque", example = "100")
        Integer quantidadeEstoque,

        @NotNull
        @PositiveOrZero(message = "Estoque mínimo não pode ser negativo")
        @Schema(description = "Estoque mínimo", example = "10")
        Integer estoqueMinimo,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Schema(description = "Preço de varejo", example = "19.90")
        BigDecimal precoVarejo,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Schema(description = "Preço de revenda", example = "12.50")
        BigDecimal precoRevenda,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Schema(description = "Custo de produção", example = "5.1234")
        BigDecimal custoProducao,

        @Schema(description = "Data prevista de lançamento", example = "2026-06-01")
        LocalDate dataPrevistaLancamento,

        @NotBlank(message = "SKU é obrigatório")
        @Size(max = 100)
        @Schema(description = "Código SKU único do produto", example = "PIN-GIRASSOL-001")
        String sku,

        @Schema(description = "URL da imagem do produto", example = "images/produto-123.jpg")
        String imgUrl,

        @Schema(description = "Peso do produto (g)", example = "13")
        Double peso,

        @Schema(description = "Altura do produto (mm)", example = "10")
        Integer altura,

        @Schema(description = "Largura do produto (mm)", example = "20")
        Integer largura,

        @Schema(description = "Comprimento do produto (mm)", example = "30")
        Integer comprimento,

        @Schema(description = "Produto ativo", example = "true")
        boolean ativo,

        @NotNull
        @Schema(description = "ID da categoria do produto", example = "2")
        Long categoriaId

) {}