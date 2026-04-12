package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.Endereco;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoPedidoDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface EnderecoMapper {

    EnderecoDTO toDTO(Endereco entity);

    EnderecoPedidoDTO toEnderecoPedidoDTO(Endereco entity);

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
