package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.Produto;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoMinDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = ProdutoVariacaoMapper.class
)
public interface ProdutoMapper {

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nome", target = "categoriaNome")
    ProdutoDTO toDTO(Produto entity);

    ProdutoMinDTO toMinDTO(Produto entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtosVariacao", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    Produto toEntity(ProdutoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(ProdutoRequest dto, @MappingTarget Produto entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(ProdutoRequest dto, @MappingTarget Produto entity);
}
