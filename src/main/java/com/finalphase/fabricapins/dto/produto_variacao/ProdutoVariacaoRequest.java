package com.finalphase.fabricapins.dto.produto_variacao;

import com.finalphase.fabricapins.domain.enums.TipoEstoqueProduto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "DTO de requisição da Variação do Produto")
public record ProdutoVariacaoRequest(
        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 150, message = "Nome do Produto precisa estar entre 3 e 150 caracteres")
        @Schema(description = "Nome do Produto", example = "Pin Girassol")
        String nome,

        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 3000, message = "Descrição precisa estar entre 3 e 3000 caracteres")
        @Schema(description = "Descrição do Produto", example = "Pin dourado metálico 30x13x2mm com acabamento esmaltado")
        String descricao,

        @NotNull
        @Schema(description = "Tipo do estoque do item", example = "PRE_VENDA")
        TipoEstoqueProduto tipoEstoque,

        @PositiveOrZero
        @Schema(description = "Quantidade do item em estoque", example = "30")
        Integer quantidadeEstoque,

        @PositiveOrZero
        @Schema(description = "Quantidade mínima em estoque para envio de alertas", example = "30")
        Integer estoqueMinimo,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Preço para clientes VAREJO", example = "3.00")
        BigDecimal precoVarejo,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Preço para clientes REVENDA", example = "2.00")
        BigDecimal precoRevenda,

        @NotNull
        @PositiveOrZero
        @Schema(description = "Custo previsto para produção do item", example = "1.00")
        BigDecimal custoProducao,

        @FutureOrPresent
        @Schema(description = "Data prevista para envio em itens Pre-Venda", example = "2026-01-30")
        LocalDate dataPrevistaEnvio,

        @NotBlank
        @Size(min = 3, max = 100, message = "SKU precisa estar entre 3 e 100 caracteres")
        @Schema(description = "Código de identificação única do produto", example = "pin5601")
        String sku,

        @Schema(description = "URL da imagem do produto", example = "images/produto-123.jpg")
        String imgUrl,

        @NotNull
        @Schema(description = "ID do produto", example = "2")
        Long produtoId
) {}
