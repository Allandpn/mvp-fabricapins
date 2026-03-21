package com.finalphase.fabricapins.integration.frete.dto;

import java.util.List;

public record MelhorEnvioCalculoRequest(
        String from,
        String to,
        List<ProdutoME> products
) {}
