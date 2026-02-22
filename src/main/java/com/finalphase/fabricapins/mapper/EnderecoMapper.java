package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Endereco;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EnderecoMapper {

    @Mapping(source = "cliente.id", target = "clienteId")
    EnderecoDTO toDTO(Endereco entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    Endereco toEntity(EnderecoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(EnderecoRequest dto, @MappingTarget Endereco entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(EnderecoRequest dto, @MappingTarget Endereco entity);
}
