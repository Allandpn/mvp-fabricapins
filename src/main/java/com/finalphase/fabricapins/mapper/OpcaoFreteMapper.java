package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.OpcaoFretePedido;
import com.finalphase.fabricapins.dto.frete.OpcaoFreteDTO;
import com.finalphase.fabricapins.integration.frete.dto.MelhorEnvioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OpcaoFreteMapper {

    OpcaoFreteDTO toDTO(OpcaoFretePedido entuty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "serviceId", source = "id")
    @Mapping(target = "nome", source = "name")
    @Mapping(target = "valor", source = "custom_price")
    @Mapping(target = "prazoDias", source = "custom_delivery_time")
    @Mapping(target = "empresa", source = "company.name")
    OpcaoFretePedido toDTOFromMelhorEnvioResponse(MelhorEnvioResponse entuty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    OpcaoFretePedido toEntity(OpcaoFreteDTO dto);
}
