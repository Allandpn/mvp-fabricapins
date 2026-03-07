package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.PedidoCupom;
import com.finalphase.fabricapins.dto.PedidoCupom.PedidoCupomDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PedidoCupomMapper {
    PedidoCupomDTO toDTO(PedidoCupom entity);

}
