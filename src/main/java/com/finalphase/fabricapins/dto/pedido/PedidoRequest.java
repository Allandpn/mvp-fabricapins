package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record PedidoRequest(
        Instant dataCriacao,
        Instant dataAtualizacao,
        StatusPedido statusPedido,
        OrigemPedido origemPedido,
        BigDecimal valorTotal,
        BigDecimal valorSubtotal,
        BigDecimal desconto,
        String numeroPedido,
        BigDecimal valorFrete,
        LocalDate dataPrevistaProducao,
        LocalDate dataConclusaoPedido,
        LocalDate dataEnvio,
        LocalDate dataEntrega,
        String nomeClienteSnapshot,
        String cpfCnpjClienteSnapshot,
        String cep,
        String estado,
        String cidade,
        String bairro,
        String logradouro,
        Integer numero,
        String complemento
) {}
