package com.finalphase.fabricapins.ecommerce.mapper;

import com.finalphase.fabricapins.ecommerce.domain.entities.ProdutoVariacao;
import com.finalphase.fabricapins.ecommerce.dto.produto_variacao.CatalogoProdutoVariacaoDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto_variacao.ProdutoVariacaoDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto_variacao.ProdutoVariacaoMinDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto_variacao.ProdutoVariacaoRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProdutoVariacaoMapper {

    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "produto.nome", target = "produtoNome")
    ProdutoVariacaoDTO toDTO(ProdutoVariacao entity);

    ProdutoVariacaoMinDTO toMinDTO(ProdutoVariacao entity);

    @Mapping(source = "produto.nome", target = "nomeProduto")
    @Mapping(source = "nome", target = "nomeVariacao")
    CatalogoProdutoVariacaoDTO toCatalogoDTO(ProdutoVariacao entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itemsPedido", ignore = true)
    @Mapping(target = "produto", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "tempoProducaoEstimado", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    ProdutoVariacao toEntity(ProdutoVariacaoRequest dto);

    @InheritConfiguration(name = "toEntity")
    void updateFromDto(ProdutoVariacaoRequest dto, @MappingTarget ProdutoVariacao entity);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(ProdutoVariacaoRequest dto, @MappingTarget ProdutoVariacao entity);
}
