package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Pedido;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoRequest;
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

    @Mapping(target = "enderecoDTO", expression = "java(mapEnderecoToDTO(entity))")
    PedidoDTO toDTO(Pedido entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "statusPedido", ignore = true)
    @Mapping(target = "valorTotal", ignore = true)
    @Mapping(target = "valorSubtotal", ignore = true)
    @Mapping(target = "desconto", ignore = true)
    @Mapping(target = "numeroPedido", ignore = true)
    @Mapping(target = "valorFrete", ignore = true)
    @Mapping(target = "dataPrevistaProducao", ignore = true)
    @Mapping(target = "dataConclusaoPedido", ignore = true)
    @Mapping(target = "dataEnvio", ignore = true)
    @Mapping(target = "dataEntrega", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "pagamento", ignore = true)
    @Mapping(target = "itemsPedido", ignore = true)
    @Mapping(target = "pedidoCupomSet", ignore = true)
    @Mapping(source="enderecoDTO.cep", target = "cep")
    @Mapping(source="enderecoDTO.estado", target = "estado")
    @Mapping(source="enderecoDTO.cidade", target = "cidade")
    @Mapping(source="enderecoDTO.bairro", target = "bairro")
    @Mapping(source="enderecoDTO.logradouro", target = "logradouro")
    @Mapping(source="enderecoDTO.numero", target = "numero")
    @Mapping(source="enderecoDTO.complemento", target = "complemento")
    @Mapping(source="enderecoDTO.pontoReferencia", target = "pontoReferencia")
    Pedido toEntity(PedidoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(PedidoRequest dto, @MappingTarget Pedido entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(PedidoRequest dto, @MappingTarget Pedido entity);


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
