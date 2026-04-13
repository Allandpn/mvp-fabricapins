package com.finalphase.fabricapins.ecommerce.dto.endereco;

public record EnderecoPedidoDTO(
        String cep,
        String estado,
        String cidade,
        String bairro,
        String logradouro,
        String numero,
        String complemento,
        String pontoReferencia
    ) {}
