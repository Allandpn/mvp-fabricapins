package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.OpcaoFretePedido;
import com.finalphase.fabricapins.dto.frete.OpcaoFreteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OpcaoFreteMapper {

    OpcaoFreteDTO toDTO(OpcaoFretePedido entuty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    OpcaoFretePedido toEntity(OpcaoFreteDTO dto);
}
