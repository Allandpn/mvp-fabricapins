package com.finalphase.fabricapins.ecommerce.dto.cliente;

import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoPessoa;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoDTO;

import java.time.Instant;
import java.util.List;

public record ClienteWtihPedidoDTO(
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
        List<EnderecoDTO> enderecos,
        List<PedidoDTO> pedidos
) {}

