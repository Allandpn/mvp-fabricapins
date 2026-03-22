package com.finalphase.fabricapins.dto.parametro;

import com.finalphase.fabricapins.domain.enums.ParametroChave;

public record ParametroDTO(
        Long id,
        ParametroChave chave,
        String valor
) {}
