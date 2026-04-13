package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.Categoria;
import com.finalphase.fabricapins.ecommerce.dto.categoria.CategoriaDTO;
import com.finalphase.fabricapins.ecommerce.dto.categoria.CategoriaRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CategoriaMapper {

    CategoriaDTO toDTO(Categoria categoria);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    Categoria toEntity(CategoriaRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto (CategoriaRequest dto, @MappingTarget Categoria entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto (CategoriaRequest dto, @MappingTarget Categoria entity);
}
