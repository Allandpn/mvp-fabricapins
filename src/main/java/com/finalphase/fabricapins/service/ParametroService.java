package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.Parametro;
import com.finalphase.fabricapins.domain.enums.ParametroChave;
import com.finalphase.fabricapins.dto.parametro.CepOrigemRequest;
import com.finalphase.fabricapins.dto.parametro.ParametroDTO;
import com.finalphase.fabricapins.exception.BusinessException;
import com.finalphase.fabricapins.mapper.ParametroMapper;
import com.finalphase.fabricapins.repository.ParametroRepository;
import jakarta.validation.Valid;
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
