package com.finalphase.fabricapins.integration.frete;

import com.finalphase.fabricapins.domain.entities.OpcaoFretePedido;
import com.finalphase.fabricapins.domain.entities.Pedido;
import com.finalphase.fabricapins.domain.enums.FreteProvider;

import java.util.List;

public interface FreteGateway {
    FreteProvider getProvider();
    List<OpcaoFretePedido> calcularFrete(Pedido pedido);
}
