package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Pedido;
import com.finalphase.fabricapins.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PedidoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "pagamento.id", target = "pagamentoId")
    PedidoDTO toDTO(Pedido entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "itemsPedido", ignore = true)
    @Mapping(target = "pedidoCupomSet", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "pagamento", ignore = true)
    Pedido toEntity(PedidoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(PedidoRequest dto, @MappingTarget Pedido entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(PedidoRequest dto, @MappingTarget Pedido entity);
}
