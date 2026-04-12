package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.Pagamento;
import com.finalphase.fabricapins.ecommerce.dto.pagamento.PagamentoDTO;
import com.finalphase.fabricapins.ecommerce.dto.pagamento.PagamentoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PagamentoMapper {

    PagamentoDTO toDTO(Pagamento entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "statusPagamento", ignore = true)
    @Mapping(target = "dataConfirmacao", ignore = true)
    @Mapping(target = "motivoRecusa", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    Pagamento toEntity(PagamentoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(PagamentoRequest dto, @MappingTarget Pagamento entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(PagamentoRequest dto, @MappingTarget Pagamento entity);
}
