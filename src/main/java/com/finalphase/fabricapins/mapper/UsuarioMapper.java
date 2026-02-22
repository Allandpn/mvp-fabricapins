package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Usuario;
import com.finalphase.fabricapins.dto.usuario.UsuarioDTO;
import com.finalphase.fabricapins.dto.usuario.UsuarioRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UsuarioMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    UsuarioDTO toDTO(Usuario entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "perfis", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    Usuario toEntity(UsuarioRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(UsuarioRequest dto, @MappingTarget Usuario entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(UsuarioRequest dto, @MappingTarget Usuario entity);
}
