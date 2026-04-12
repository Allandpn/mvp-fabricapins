package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.CupomDesconto;
import com.finalphase.fabricapins.ecommerce.exception.BusinessException;
import com.finalphase.fabricapins.ecommerce.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.ecommerce.repository.CupomDescontoRepository;
import com.finalphase.fabricapins.ecommerce.repository.PedidoCupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
