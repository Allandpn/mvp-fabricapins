package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Perfil;
import com.finalphase.fabricapins.dto.perfil.PerfilDTO;
import com.finalphase.fabricapins.dto.perfil.PerfilRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PerfilMapper {

    PerfilDTO toDTO(Perfil entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    Perfil toEntity(PerfilRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(PerfilRequest dto, @MappingTarget Perfil entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(PerfilRequest dto, @MappingTarget Perfil entity);
}
