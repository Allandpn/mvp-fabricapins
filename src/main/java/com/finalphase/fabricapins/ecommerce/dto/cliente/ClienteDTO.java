package com.finalphase.fabricapins.ecommerce.dto.cliente;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoPessoa;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoPedidoRequest;

import java.time.Instant;
import java.util.List;

public record ClienteDTO(
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
        List<EnderecoPedidoRequest> enderecos
) {}

