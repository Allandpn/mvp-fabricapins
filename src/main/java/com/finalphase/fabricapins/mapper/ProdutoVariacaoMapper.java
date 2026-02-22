package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.ProdutoVariacao;
import com.finalphase.fabricapins.dto.produto_variacao.ProdutoVariacaoDTO;
import com.finalphase.fabricapins.dto.produto_variacao.ProdutoVariacaoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProdutoVariacaoMapper {

    @Mapping(source = "produto.id", target = "produtoId")
    ProdutoVariacaoDTO toDTO(ProdutoVariacao entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itemsPedido", ignore = true)
    @Mapping(target = "produto", ignore = true)
    ProdutoVariacao toEntity(ProdutoVariacaoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(ProdutoVariacaoRequest dto, @MappingTarget ProdutoVariacao entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(ProdutoVariacaoRequest dto, @MappingTarget ProdutoVariacao entity);
}
