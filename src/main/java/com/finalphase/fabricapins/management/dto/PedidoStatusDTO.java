package com.finalphase.fabricapins.management.dto;
import com.finalphase.fabricapins.ecommerce.domain.enums.StatusPedido;

public record PedidoStatusDTO(
        StatusPedido status,
        Integer quantidade
) {}
