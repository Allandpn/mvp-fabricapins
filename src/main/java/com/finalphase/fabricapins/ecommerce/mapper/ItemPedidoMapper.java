package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.ItemPedido;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.ItemPedidoDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ItemPedidoMapper {

    @Mapping(source = "produto.id", target ="id")
    @Mapping(source = "produto.nome", target ="nome")
    ItemPedidoDTO toDTO(ItemPedido entity);

//    ItemPedido toEntity(ItemPedidoDTO dto);

//    void updateFromDto(ItemPedidoRequest dto, @MappingTarget ItemPedido entity);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void partialUpdateFromDto(ItemPedidoRequest dto, @MappingTarget ItemPedido entity);


}
