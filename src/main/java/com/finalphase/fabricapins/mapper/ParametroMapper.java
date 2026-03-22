package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Parametro;
import com.finalphase.fabricapins.dto.parametro.ParametroDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ParametroMapper {
    ParametroDTO toDTO(Parametro entity);
}
