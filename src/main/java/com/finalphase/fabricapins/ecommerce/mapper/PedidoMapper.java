package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.Cliente;
import com.finalphase.fabricapins.ecommerce.domain.entities.Pedido;
import com.finalphase.fabricapins.ecommerce.dto.cliente.ClienteMinPedidoDTO;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoAdminDTO;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoAdminRequest;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.ecommerce.dto.pedido.PedidoMinDTO;
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
    @Mapping(target = "cliente", expression = "java(mapCliente(entity))")
    PedidoDTO toDTO(Pedido entity);

    PedidoMinDTO toMinDTO(Pedido entity);

    PedidoAdminDTO toAdminDTO(Pedido entity);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "dataCriacao", ignore = true)
//    @Mapping(target = "dataAtualizacao", ignore = true)
//    @Mapping(target = "dataConclusaoPedido", ignore = true)
//    @Mapping(target = "statusPedido", ignore = true)
//    @Mapping(target = "valorTotal", ignore = true)
//    @Mapping(target = "valorSubtotal", ignore = true)
//    @Mapping(target = "desconto", ignore = true)
//    @Mapping(target = "codigoPedido", ignore = true)
//    @Mapping(target = "freteServiceId", ignore = true)
//    @Mapping(target = "dataCalculoFrete", ignore = true)
//    @Mapping(target = "valorFrete", ignore = true)
//    @Mapping(target = "nomeServicoFrete", ignore = true)
//    @Mapping(target = "prazoEntregaDias", ignore = true)
//    @Mapping(target = "freteEmpresa", ignore = true)
//    @Mapping(target = "dataEnvio", ignore = true)
//    @Mapping(target = "dataEntrega", ignore = true)
//    @Mapping(target = "cliente", ignore = true)
//    @Mapping(target = "pagamento", ignore = true)
//    @Mapping(target = "valorTotalFinal", ignore = true)
//    @Mapping(target = "valorFreteFinal", ignore = true)
//    @Mapping(target = "valorDescontoFinal", ignore = true)
//    @Mapping(target = "itemsPedido", ignore = true)
//    @Mapping(target = "cupons", ignore = true)
//    @Mapping(target = "opcoesFrete", ignore = true)
//    @Mapping(source="enderecoEntrega.cep", target = "cep")
//    @Mapping(source="enderecoEntrega.estado", target = "estado")
//    @Mapping(source="enderecoEntrega.cidade", target = "cidade")
//    @Mapping(source="enderecoEntrega.bairro", target = "bairro")
//    @Mapping(source="enderecoEntrega.logradouro", target = "logradouro")
//    @Mapping(source="enderecoEntrega.numero", target = "numero")
//    @Mapping(source="enderecoEntrega.complemento", target = "complemento")
//    @Mapping(source="enderecoEntrega.pontoReferencia", target = "pontoReferencia")
//    Pedido toEntity(PedidoAdminRequest dto);

//    @InheritConfiguration(name = "toEntity")
//    void updateFromDto(PedidoAdminRequest dto, @MappingTarget Pedido entity);
//
//    @InheritConfiguration(name = "toEntity")
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void partialUpdateFromDto(PedidoAdminRequest dto, @MappingTarget Pedido entity);


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

    default ClienteMinPedidoDTO mapCliente(Pedido pedido){
        if(pedido.getCliente() != null){
            Cliente c = pedido.getCliente();
            return new ClienteMinPedidoDTO(
                    c.getId(),
                    c.getNome(),
                    c.getNumeroDocumento(),
                    c.getTelefone(),
                    c.getTipoCliente()
            );
        }
        return new ClienteMinPedidoDTO(
                null,
                pedido.getNomeCliente(),
                pedido.getDocumentoCliente(),
                pedido.getTelefone(),
                pedido.getTipoCliente()
        );
    }
}
