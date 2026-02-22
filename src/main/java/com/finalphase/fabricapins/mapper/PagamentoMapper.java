package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Pagamento;
import com.finalphase.fabricapins.dto.pagamento.PagamentoDTO;
import com.finalphase.fabricapins.dto.pagamento.PagamentoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PagamentoMapper {

    @Mapping(source = "pedido.id", target = "pedidoId")
    PagamentoDTO toDTO(Pagamento entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "dataConfirmacao", ignore = true)
    Pagamento toEntity(PagamentoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(PagamentoRequest dto, @MappingTarget Pagamento entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(PagamentoRequest dto, @MappingTarget Pagamento entity);
}
