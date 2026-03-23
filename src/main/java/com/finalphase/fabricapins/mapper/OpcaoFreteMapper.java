package com.finalphase.fabricapins.mapper;

import com.finalphase.fabricapins.domain.entities.OpcaoFretePedido;
import com.finalphase.fabricapins.dto.frete.OpcaoFreteDTO;
import com.finalphase.fabricapins.integration.frete.dto.MelhorEnvioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface OpcaoFreteMapper {

    OpcaoFreteDTO toDTO(OpcaoFretePedido entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "serviceId", source = "id")
    @Mapping(target = "nome", source = "name")
    @Mapping(target = "valor", source = "custom_price", qualifiedByName = "stringToBigDecimal")
    @Mapping(target = "prazoDias", source = "custom_delivery_time")
    @Mapping(target = "empresa", source = "company.name")
    @Mapping(target = "provider", constant = "MELHOR_ENVIO")
    OpcaoFretePedido toEntityFromMelhorEnvioResponse(MelhorEnvioResponse dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "provider", ignore = true)
    OpcaoFretePedido toEntity(OpcaoFreteDTO dto);

    @Named("stringToBigDecimal")
    static BigDecimal stringToBigDecimal(String value){
        if(value == null || value.isBlank()){
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }
}
