package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Pedido;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoAdminRequest;
import com.finalphase.fabricapins.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoMinDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoRascunhoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {ClienteMapper.class,
                PagamentoMapper.class,
                ItemPedidoMapper.class,
                CupomDescontoMapper.class,
                PedidoCupomMapper.class
        }
)
public interface PedidoMapper {

    @Mapping(source = "itemsPedido", target = "items")
    @Mapping(target = "enderecoEntrega", expression = "java(mapEnderecoToDTO(entity))")
    PedidoDTO toDTO(Pedido entity);

    PedidoMinDTO toMinDTO(Pedido entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "statusPedido", ignore = true)
    @Mapping(target = "valorTotal", ignore = true)
    @Mapping(target = "valorSubtotal", ignore = true)
    @Mapping(target = "desconto", ignore = true)
    @Mapping(target = "codigoPedido", ignore = true)
    @Mapping(target = "valorFrete", ignore = true)
    @Mapping(target = "dataPrevistaProducao", ignore = true)
    @Mapping(target = "dataConclusaoPedido", ignore = true)
    @Mapping(target = "dataEnvio", ignore = true)
    @Mapping(target = "dataEntrega", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "pagamento", ignore = true)
    @Mapping(target = "itemsPedido", ignore = true)
    @Mapping(target = "cupons", ignore = true)
    @Mapping(source="enderecoEntrega.cep", target = "cep")
    @Mapping(source="enderecoEntrega.estado", target = "estado")
    @Mapping(source="enderecoEntrega.cidade", target = "cidade")
    @Mapping(source="enderecoEntrega.bairro", target = "bairro")
    @Mapping(source="enderecoEntrega.logradouro", target = "logradouro")
    @Mapping(source="enderecoEntrega.numero", target = "numero")
    @Mapping(source="enderecoEntrega.complemento", target = "complemento")
    @Mapping(source="enderecoEntrega.pontoReferencia", target = "pontoReferencia")
    Pedido toEntity(PedidoAdminRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(PedidoAdminRequest dto, @MappingTarget Pedido entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(PedidoAdminRequest dto, @MappingTarget Pedido entity);


    //HELPERS
    default EnderecoPedidoDTO mapEnderecoToDTO(Pedido pedido){
        return new EnderecoPedidoDTO(
                pedido.getCep(),
                pedido.getEstado(),
                pedido.getCidade(),
                pedido.getBairro(),
                pedido.getLogradouro(),
                pedido.getNumero(),
                pedido.getComplemento(),
                pedido.getPontoReferencia()
        );
    }
}
