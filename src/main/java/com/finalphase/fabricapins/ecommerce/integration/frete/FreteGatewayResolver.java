package com.finalphase.fabricapins.ecommerce.integration.frete;

import com.finalphase.fabricapins.ecommerce.domain.enums.FreteProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FreteGatewayResolver {
    private final Map<FreteProvider, FreteGateway> gateways;
    public FreteGatewayResolver(List<FreteGateway> gatewaysList){
        this.gateways = gatewaysList.stream().collect(Collectors.toMap(FreteGateway::getProvider, g -> g));
    }
    public FreteGateway resolve(FreteProvider provider){
        return gateways.get(provider);
    }
}
