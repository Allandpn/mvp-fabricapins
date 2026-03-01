package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Usuario;
import com.finalphase.fabricapins.dto.usuario.UsuarioDTO;
import com.finalphase.fabricapins.dto.usuario.UsuarioRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UsuarioMapper {

    UsuarioDTO toDTO(Usuario entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "perfis", ignore = true)
    void updateFromDto(UsuarioRequest dto, @MappingTarget Usuario entity);

    @InheritConfiguration(name = "updateFromDto")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(UsuarioRequest dto, @MappingTarget Usuario entity);

}
