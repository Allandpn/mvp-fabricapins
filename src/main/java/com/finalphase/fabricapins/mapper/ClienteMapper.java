package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Cliente;
import com.finalphase.fabricapins.dto.cliente.ClienteDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ClienteMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    ClienteDTO toDTO(Cliente entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "pedidos", ignore = true)
    @Mapping(target = "enderecos", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Cliente toEntity(ClienteRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(ClienteRequest dto, @MappingTarget Cliente entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(ClienteRequest dto, @MappingTarget Cliente entity);
}
