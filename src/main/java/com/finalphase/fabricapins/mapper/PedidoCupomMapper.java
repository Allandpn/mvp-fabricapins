package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.PedidoCupom;
import com.finalphase.fabricapins.dto.PedidoCupom.PedidoCupomDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PedidoCupomMapper {

    @Mapping(source = "codigoCupom", target = "codigo")
    PedidoCupomDTO toDTO(PedidoCupom entity);

    List<PedidoCupomDTO> toDTOSet(Set<PedidoCupom> entities);

}
