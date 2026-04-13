package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.PedidoCupom;
import com.finalphase.fabricapins.ecommerce.dto.PedidoCupom.PedidoCupomDTO;
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
