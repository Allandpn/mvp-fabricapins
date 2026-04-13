package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.Perfil;
import com.finalphase.fabricapins.ecommerce.dto.perfil.PerfilMinDTO;
import com.finalphase.fabricapins.ecommerce.dto.perfil.PerfilRequest;
import com.finalphase.fabricapins.ecommerce.dto.perfil.PerfilWithUsuariosDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface PerfilMapper {

    PerfilMinDTO toMinDTO(Perfil entity);

    PerfilWithUsuariosDTO toWithUsersDTO(Perfil entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    Perfil toEntity(PerfilRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(PerfilRequest dto, @MappingTarget Perfil entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(PerfilRequest dto, @MappingTarget Perfil entity);
}
