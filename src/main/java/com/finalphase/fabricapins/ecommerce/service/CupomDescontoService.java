package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.CupomDesconto;
import com.finalphase.fabricapins.ecommerce.dto.cupom_desconto.CupomDescontoDTO;
import com.finalphase.fabricapins.ecommerce.dto.cupom_desconto.CupomDescontoRequest;
import com.finalphase.fabricapins.ecommerce.exception.BusinessException;
import com.finalphase.fabricapins.ecommerce.exception.DatabaseException;
import com.finalphase.fabricapins.ecommerce.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.ecommerce.mapper.CupomDescontoMapper;
import com.finalphase.fabricapins.ecommerce.repository.CupomDescontoRepository;
import com.finalphase.fabricapins.ecommerce.repository.PedidoCupomRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CupomDescontoService {

    @Autowired
    private CupomDescontoRepository repository;
    @Autowired
    private PedidoCupomRepository pedidoCupomRepository;
    @Autowired
    private CupomDescontoMapper mapper;

    @Transactional(readOnly = true)
    public CupomDescontoDTO findById(Long id) {
        CupomDesconto entity = repository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Cupom não encontrado")
        );
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<CupomDescontoDTO> findAll() {
        return repository.findAllByAtivoTrue().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional
    public CupomDescontoDTO insertCupom(@Valid CupomDescontoRequest request) {
        String codigo = request.codigo();
        if (codigo != null && repository.existsByCodigo(codigo)) {
            throw new DatabaseException("Já existe um cupom com esse código");
        }
        CupomDesconto entity = mapper.toEntity(request);
        try {
            entity.setAtivo(true);
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível cadastrar o cupom");
        }
        return mapper.toDTO(entity);
    }

    @Transactional
    public CupomDescontoDTO updateCupom(Long id, @Valid CupomDescontoRequest request) {
        CupomDesconto entity = repository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Cupom não encontrado")
        );
        String codigo = request.codigo();
        if (codigo != null && repository.existsByCodigoAndIdNot(codigo, id)) {
            throw new DatabaseException("Já existe um cupom com esse código");
        }
        mapper.updateFromDto(request, entity);
        return mapper.toDTO(entity);
    }

    @Transactional
    public void deleteCupom(Long id) {
        CupomDesconto entity = repository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Cupom não encontrado")
        );
        entity.setAtivo(false);
    }

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
