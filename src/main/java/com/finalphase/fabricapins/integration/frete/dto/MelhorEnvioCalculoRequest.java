package com.finalphase.fabricapins.integration.frete.dto;

import java.util.List;

public record MelhorEnvioCalculoRequest(
        EnderecoME from,
        EnderecoME to,
        List<ProdutoME> products,
        OptionsME options,
        String services
) {}
