package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.Parametro;
import com.finalphase.fabricapins.ecommerce.dto.parametro.ParametroDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ParametroMapper {
    ParametroDTO toDTO(Parametro entity);
}
