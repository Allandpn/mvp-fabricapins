package com.finalphase.fabricapins.ecommerce.dto.cliente;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoPessoa;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoPedidoRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "DTO de requisição do Cliente")
public record ClienteRequest(
        @NotBlank(message = "Campo requerido")
        @Size(min = 3, max = 150, message = "Nome do Cliente precisa estar entre 3 e 150 caracteres")
        @Schema(description = "Nome do Cliente", example = "Maria da Silva")
        String nome,

        @NotBlank(message = "Campo requerido")
        @Email
        @Schema(description = "Email do Cliente", example = "maria_silva@email.com")
        String email,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Telefone do Cliente", example = "49999999999")
        String telefone,

        @NotNull(message = "Campo requerido")
        @Schema(description = "Tipo de Cliente", example = "VAREJO")
        TipoCliente tipoCliente,

        @NotNull(message = "Campo requerido")
        @Schema(description = "Tipo de Pessoa", example = "FISICA")
        TipoPessoa tipoPessoa,

        @NotBlank(message = "Campo requerido")
        @Schema(description = "Numero do Documento", example = "00055522266")
        String numeroDocumento,

        @Schema(description = "Cliente ativo", example = "true")
        boolean ativo,

        @NotEmpty(message = "Cliente deve possuir ao menos um endereço")
        List<EnderecoPedidoRequest>enderecos
) {}
