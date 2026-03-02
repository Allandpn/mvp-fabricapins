package com.finalphase.fabricapins.dto.cliente;

import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.domain.enums.TipoPessoa;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

        @NotNull(message = "Campo requerido")
        @Schema(description = "Lista de Enderecos", example = "[{ . . . }]")
        List<EnderecoDTO> enderecos
) {}
