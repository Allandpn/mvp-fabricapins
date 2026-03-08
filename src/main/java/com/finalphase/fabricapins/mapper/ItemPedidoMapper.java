package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.ItemPedido;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ItemPedidoMapper {

    @Mapping(source = "produtoVariacao.id", target ="produtoVariacaoId")
    @Mapping(source = "produtoVariacao.nome", target ="produtoVariacaoNome")
    ItemPedidoDTO toDTO(ItemPedido entity);

//    ItemPedido toEntity(ItemPedidoDTO dto);

//    void updateFromDto(ItemPedidoRequest dto, @MappingTarget ItemPedido entity);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void partialUpdateFromDto(ItemPedidoRequest dto, @MappingTarget ItemPedido entity);


}
