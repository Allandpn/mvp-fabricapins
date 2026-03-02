package com.finalphase.fabricapins.dto.cliente;

import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.domain.enums.TipoPessoa;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;

import java.time.Instant;
import java.util.List;

public record ClienteMinDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        TipoPessoa tipoPessoa,
        String numeroDocumento,
        TipoCliente tipoCliente,
        Instant dataCadastro,
        Instant dataAtualizacao,
        boolean ativo,
        String nomeUsuario,
        List<EnderecoDTO> enderecos
) {}

