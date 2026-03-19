package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.Cliente;
import com.finalphase.fabricapins.domain.entities.Endereco;
import com.finalphase.fabricapins.dto.cliente.ClienteMinDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteMinPedidoDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteRequest;
import com.finalphase.fabricapins.dto.cliente.ClienteWtihPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = { PedidoMapper.class},
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ClienteMapper {

    ClienteMinDTO toDTO(Cliente entity);

    ClienteMinPedidoDTO toClienteMinPedidoDTO(Cliente entity);

//    ClienteWtihPedidoDTO toDTOWithPedido(Cliente entity);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "dataCadastro", ignore = true)
//    @Mapping(target = "dataAtualizacao", ignore = true)
//    @Mapping(target = "pedidos", ignore = true)
//    @Mapping(target = "usuario", ignore = true)
//    @Mapping(target = "enderecos", ignore = true)
//    Cliente toEntity(ClienteRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCadastro", ignore = true)
    @Mapping(target = "dataAtualizacao", ignore = true)
    @Mapping(target = "pedidos", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "enderecos", ignore = true)
    void updateFromDto(ClienteRequest dto, @MappingTarget Cliente entity);

    @InheritConfiguration(name = "updateFromDto")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdateFromDto(ClienteRequest dto, @MappingTarget Cliente entity);

}
