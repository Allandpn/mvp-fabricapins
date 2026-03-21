package com.finalphase.fabricapins.integration.frete;

import com.finalphase.fabricapins.domain.entities.OpcaoFretePedido;
import com.finalphase.fabricapins.domain.entities.Pedido;
import com.finalphase.fabricapins.domain.enums.FreteProvider;
import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.integration.frete.client.MelhorEnvioClient;
import com.finalphase.fabricapins.integration.frete.dto.MelhorEnvioCalculoRequest;
import com.finalphase.fabricapins.integration.frete.dto.ProdutoME;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MelhorEnvioGateway implements FreteGateway{

    private final MelhorEnvioClient client;

    public MelhorEnvioGateway(MelhorEnvioClient client){
        this.client = client;
    }

    @Override
    public FreteProvider getProvider() {
        return FreteProvider.MELHOR_ENVIO;
    }

    @Override
    public List<OpcaoFretePedido> calcularFrete(Pedido pedido) {
        Object request = montarRequest(pedido);

        String response = client.calcularFrete(request, "TOKEN");

        return List.of(
                new OpcaoFretePedido("1", "PAC", new BigDecimal("20.00"), 5, "MELHOR_ENVIO")
        );
    }


    private MelhorEnvioCalculoRequest montarRequest(Pedido pedido){
        String cepOrigem = "64300000";
        String cepDestino = pedido.getCep();

        List<ProdutoME> produtos = pedido.getItemsPedido().stream()
                .map(item -> new ProdutoME(
                        item.getProdutoVariacao().getId().toString(),
                        item.getProdutoVariacao().getProduto().getLargura(),
                        item.getProdutoVariacao().getProduto().getAltura(),
                        item.getProdutoVariacao().getProduto().getComprimento(),
                        item.getProdutoVariacao().getProduto().getPeso(),
                        pedido.getTipoCliente().equals(TipoCliente.VAREJO)?Double.valueOf(String.valueOf(item.getProdutoVariacao().getPrecoVarejo())):Double.valueOf(String.valueOf(item.getProdutoVariacao().getPrecoRevenda())),
                        item.getQuantidade())).toList();

        return new MelhorEnvioCalculoRequest(
                cepOrigem,
                cepDestino,
                produtos
        );
    }


}
