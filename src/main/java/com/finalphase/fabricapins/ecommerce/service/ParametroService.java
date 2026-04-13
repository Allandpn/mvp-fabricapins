package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.Parametro;
import com.finalphase.fabricapins.ecommerce.domain.enums.ParametroChave;
import com.finalphase.fabricapins.ecommerce.dto.parametro.ParametroDTO;
import com.finalphase.fabricapins.ecommerce.exception.BusinessException;
import com.finalphase.fabricapins.ecommerce.mapper.ParametroMapper;
import com.finalphase.fabricapins.ecommerce.repository.ParametroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParametroService {

    @Autowired
    private ParametroRepository repository;

    @Autowired
    private ParametroMapper mapper;

    public void atualizarCepOrigem(String cep) {
        validarCep(cep);

        Parametro parametro = repository.findByChave(ParametroChave.CEP_ORIGEM).orElse(
                new Parametro(ParametroChave.CEP_ORIGEM, cep)
        );
        parametro.atualizarValor(cep);
        repository.save(parametro);
    }

    private void validarCep(String cep) {
        if(!cep.matches("\\d{8}")){
            throw new BusinessException("CEP inválido");
        }
    }

    public ParametroDTO getParametro(ParametroChave parametroChave) {
        Parametro parametro = repository.findByChave(parametroChave).orElseThrow(
                () ->  new BusinessException("Parâmetro [" + parametroChave + "] não encontrado")
        );
        return mapper.toDTO(parametro);
    }
}
