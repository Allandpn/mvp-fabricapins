package com.finalphase.fabricapins.ecommerce.dto.cupom_desconto;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoDesconto;
import com.finalphase.fabricapins.ecommerce.dto.PedidoCupom.PedidoCupomDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record CupomDescontoDTO(
        Long id,
        String codigo,
        boolean ativo,
        BigDecimal valorDesconto,
        TipoDesconto tipoDesconto,
        LocalDate dataValidade,
        Integer quantidadeMinimaItens,
        BigDecimal valorMinimoPedido,
        Integer limiteUsos
) {}


