package com.finalphase.fabricapins.dto.endereco;

import com.finalphase.fabricapins.domain.enums.TipoEndereco;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Schema(description = "DTO de requisição do Endereço")
public record EnderecoDTO(
        @NotBlank(message = "Campo requerido")
        @Schema(description = "CEP", example = "89000000")
        String cep,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Estado", example = "Sao Paulo")
        String estado,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Cidade", example = "Campinas")
        String cidade,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Bairro", example = "Monte Alegre")
        String bairro,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Logradouro", example = "Rua XV de Novembro")
        String logradouro,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Número", example = "S/N")
        String numero,

        @Schema(description = "Complemento", example = "casa 03")
        String complemento,

        @Schema(description = "Ponto de Referencia", example = "Próximo Praça da Luz")
        String pontoReferencia,

        @Schema(description = "Observações", example = "Entregar preferencialmente pela manhã")
        String observacoes,

        @Schema(description = "Endereço principal", example = "true")
        boolean enderecoPrincipal,

        @NotNull(message = "Campo requerido")
        @Schema(description = "Tipo de Endereço", example = "ENTREGA")
        TipoEndereco tipoEndereco,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Apelido", example = "Minha casa")
        String apelido,

        @Schema(description = "Data Criacao do Endereco (preenchida automaticamente)", example = "2026-01-01T19:05:46.393482Z")
        Instant dataCadastro
) {}
