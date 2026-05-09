package com.finalphase.fabricapins.management.dto;

import java.util.List;

public record ProducaoDTO(
        Double tempoMedioProducaoHoras,
        Integer quantidadeProntaEntrega,
        Integer quantidadePreVenda,
        Integer quantidadeSobDemanda,
        List<PedidoStatusDTO> pedidosPorStatus,
        List<DuracaoProducaoDTO> produtosMaisDemorados

) {}

