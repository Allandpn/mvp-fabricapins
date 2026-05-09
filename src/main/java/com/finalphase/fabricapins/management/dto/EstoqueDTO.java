package com.finalphase.fabricapins.management.dto;

import java.util.List;

public record EstoqueDTO(
        List<ProdutoAnalitcsDTO> produtos,
        Integer estoqueCritico,
        Integer estoqueExcesso
) {}
