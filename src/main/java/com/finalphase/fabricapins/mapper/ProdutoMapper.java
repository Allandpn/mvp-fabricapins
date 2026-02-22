package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Produto;
import com.finalphase.fabricapins.dto.produto.ProdutoDTO;
import com.finalphase.fabricapins.dto.produto.ProdutoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProdutoMapper {

    @Mapping(source = "categoria.id", target = "categoriaId")
    ProdutoDTO toDTO(Produto entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtosVariacao", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    Produto toEntity(ProdutoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(ProdutoRequest dto, @MappingTarget Produto entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(ProdutoRequest dto, @MappingTarget Produto entity);
}
