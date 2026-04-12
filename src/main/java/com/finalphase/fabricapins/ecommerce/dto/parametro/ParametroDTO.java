package com.finalphase.fabricapins.ecommerce.dto.parametro;

import com.finalphase.fabricapins.ecommerce.domain.enums.ParametroChave;

public record ParametroDTO(
        Long id,
        ParametroChave chave,
        String valor
) {}
