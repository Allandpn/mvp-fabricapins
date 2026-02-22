package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.CupomDesconto;
import com.finalphase.fabricapins.dto.cupom_desconto.CupomDescontoDTO;
import com.finalphase.fabricapins.dto.cupom_desconto.CupomDescontoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CupomDescontoMapper {

    CupomDescontoDTO toDTO(CupomDesconto entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedidoCupomSet", ignore = true)
    CupomDesconto toEntity(CupomDescontoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(CupomDescontoRequest dto, @MappingTarget CupomDesconto entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(CupomDescontoRequest dto, @MappingTarget CupomDesconto entity);
}
