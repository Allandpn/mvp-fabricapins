package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.CupomDesconto;
import com.finalphase.fabricapins.exception.BusinessException;
import com.finalphase.fabricapins.exception.DateOutOfBoundsException;
import com.finalphase.fabricapins.exception.InsufficientStockException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.repository.CupomDescontoRepository;
import com.finalphase.fabricapins.repository.PedidoCupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CupomDescontoService {

    @Autowired
    private CupomDescontoRepository repository;
    @Autowired
    private PedidoCupomRepository pedidoCupomRepository;

    @Transactional(readOnly = true)
    public CupomDesconto findByCodigo(String codigo){
        CupomDesconto cupom = repository.findByCodigoAndAtivoTrue(codigo).orElseThrow(
                () -> new ResourceNotFoundException("Cupom " + codigo + " não encontrado")
        );
        return cupom;
    }

    public void validarLimiteUso(CupomDesconto cupom){
        if(cupom.getLimiteUsos() == null){
            return;
        }
        long usos = pedidoCupomRepository.countByCupomDescontoId(cupom.getId());

        if(usos >= cupom.getLimiteUsos()){
            throw new BusinessException("Cupom " + cupom.getCodigo() + " esgotado");
        }
    }


}
