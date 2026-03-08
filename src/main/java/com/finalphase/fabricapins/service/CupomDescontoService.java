package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.CupomDesconto;
import com.finalphase.fabricapins.exception.DateOutOfBoundsException;
import com.finalphase.fabricapins.exception.InsufficientStockException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.repository.CupomDescontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class CupomDescontoService {

    @Autowired
    private CupomDescontoRepository repository;

    @Transactional(readOnly = true)
    public CupomDesconto findByCodigo(String codigo){
        CupomDesconto cupom = repository.findByCodigoAndAtivoTrue(codigo).orElseThrow(
                () -> new ResourceNotFoundException("Cupom não encontrado")
        );
        if(cupom.getDataValidade().plusDays(1).atStartOfDay().isBefore(LocalDateTime.now())){;
            throw new DateOutOfBoundsException("Cupom expirado");
        }
        if(cupom.getLimiteUsos() == 0){
            throw new InsufficientStockException("Cupom atingiu limite de uso");
        }
        return cupom;
    }
}
