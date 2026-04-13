package com.finalphase.fabricapins.ecommerce.integration.frete;

import com.finalphase.fabricapins.ecommerce.domain.entities.OpcaoFretePedido;
import com.finalphase.fabricapins.ecommerce.domain.entities.Pedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.FreteProvider;

import java.util.List;

public interface FreteGateway {
    FreteProvider getProvider();
    List<OpcaoFretePedido> calcularFrete(Pedido pedido);
}
