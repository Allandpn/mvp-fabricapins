package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Endereco;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EnderecoMapper {

    EnderecoDTO toDTO(Endereco entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    Endereco toEntity(EnderecoDTO dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(EnderecoDTO dto, @MappingTarget Endereco entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(EnderecoDTO dto, @MappingTarget Endereco entity);
}
