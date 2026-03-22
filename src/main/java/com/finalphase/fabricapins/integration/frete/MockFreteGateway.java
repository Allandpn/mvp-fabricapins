package com.finalphase.fabricapins.integration.frete;

import com.finalphase.fabricapins.domain.entities.OpcaoFretePedido;
import com.finalphase.fabricapins.domain.entities.Pedido;
import com.finalphase.fabricapins.domain.enums.FreteProvider;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MockFreteGateway implements FreteGateway {
    @Override
    public FreteProvider getProvider() {
        return FreteProvider.MOCK;
    }
    @Override
    public List<OpcaoFretePedido> calcularFrete(Pedido pedido) {
        return List.of(
        new OpcaoFretePedido("1","PAC",new BigDecimal("15.00"),5,"MOCK"),
        new OpcaoFretePedido("2","SEDEX",new BigDecimal("25.00"),2,"MOCK")
        );
    }
}
