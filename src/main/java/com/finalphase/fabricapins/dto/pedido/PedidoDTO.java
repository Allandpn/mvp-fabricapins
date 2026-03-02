package com.finalphase.fabricapins.dto.pedido;

import com.finalphase.fabricapins.domain.entities.Cliente;
import com.finalphase.fabricapins.domain.entities.ItemPedido;
import com.finalphase.fabricapins.domain.entities.Pagamento;
import com.finalphase.fabricapins.domain.entities.PedidoCupom;
import com.finalphase.fabricapins.domain.enums.OrigemPedido;
import com.finalphase.fabricapins.domain.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record PedidoDTO(
        Long id,
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
        String complemento,
        Cliente cliente,
        Pagamento pagamento,
        List<ItemPedido> itemsPedido,
        Set<PedidoCupom> pedidoCupomSet
) {}
