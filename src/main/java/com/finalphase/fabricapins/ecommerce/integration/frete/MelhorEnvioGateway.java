package com.finalphase.fabricapins.ecommerce.integration.frete;

import com.finalphase.fabricapins.ecommerce.domain.entities.OpcaoFretePedido;
import com.finalphase.fabricapins.ecommerce.domain.entities.Pedido;
import com.finalphase.fabricapins.ecommerce.domain.enums.FreteProvider;
import com.finalphase.fabricapins.ecommerce.domain.enums.ParametroChave;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.dto.parametro.ParametroDTO;
import com.finalphase.fabricapins.ecommerce.integration.frete.client.MelhorEnvioClient;
import com.finalphase.fabricapins.ecommerce.integration.frete.dto.*;
import com.finalphase.fabricapins.ecommerce.mapper.OpcaoFreteMapper;
import com.finalphase.fabricapins.ecommerce.service.ParametroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class MelhorEnvioGateway implements FreteGateway{

    @Autowired
    private ParametroService parametroService;

    @Autowired
    private OpcaoFreteMapper mapper;

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

        List<MelhorEnvioResponse> response = client.calcularFrete(request);

        return response.stream()
                .filter(r -> r.error() == null)
                .map(mapper::toEntityFromMelhorEnvioResponse)
                .sorted(Comparator.comparing(OpcaoFretePedido::getValor))
                .toList();
    }


    private MelhorEnvioCalculoRequest montarRequest(Pedido pedido){
        ParametroDTO cepOrigem = parametroService.getParametro(ParametroChave.CEP_ORIGEM);
        EnderecoME from = new EnderecoME(cepOrigem.valor());
        EnderecoME to =  new EnderecoME(pedido.getCep());

        List<ProdutoME> produtos = pedido.getItemsPedido().stream()
                .map(item -> new ProdutoME(
                        item.getProdutoVariacao().getId().toString(),
                        item.getProdutoVariacao().getProduto().getLargura(),
                        item.getProdutoVariacao().getProduto().getAltura(),
                        item.getProdutoVariacao().getProduto().getComprimento(),
                        item.getProdutoVariacao().getProduto().getPeso(),
                        pedido.getTipoCliente().equals(TipoCliente.VAREJO)
                                ?item.getProdutoVariacao().getPrecoVarejo().doubleValue()
                                :item.getProdutoVariacao().getPrecoRevenda().doubleValue(),
                        item.getQuantidade()
                ))
                .toList();

        return new MelhorEnvioCalculoRequest(
                from,
                to,
                produtos,
                new OptionsME(false, false),
                "1,2,18"
        );
    }


}
