package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.Categoria;
import com.finalphase.fabricapins.ecommerce.dto.categoria.CategoriaDTO;
import com.finalphase.fabricapins.ecommerce.dto.categoria.CategoriaRequest;
import com.finalphase.fabricapins.ecommerce.exception.DatabaseException;
import com.finalphase.fabricapins.ecommerce.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.ecommerce.mapper.CategoriaMapper;
import com.finalphase.fabricapins.ecommerce.repository.CategoriaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private CategoriaMapper mapper;

    @Transactional(readOnly = true)
    public CategoriaDTO findById(Long id) {
        Categoria entity = repository.findByIdAndAtivaTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        List<Categoria> result = repository.findAllByAtivaTrue();
        return result.stream().map(mapper::toDTO).toList();
    }

    @Transactional()
    public CategoriaDTO insertCategoria(CategoriaRequest request) {
        if(repository.existsByNome(request.nome())){
            throw new DatabaseException("Já existe uma categoria com esse nome");
        }
        Categoria entity = mapper.toEntity(request);
        try{
            entity.setAtiva(true);
            repository.save(entity);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível cadastrar a Categoria");
        }
        return mapper.toDTO(entity);
    }

    @Transactional()
    public CategoriaDTO updateCategoria(Long id, CategoriaRequest request) {
        Categoria entity = repository.findByIdAndAtivaTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );

        if(repository.existsByNomeAndIdNot(request.nome(), id)){
            throw new DatabaseException("Já existe uma categoria com esse numero de nome");
        }
        mapper.updateFromDto(request, entity);
        return mapper.toDTO(entity);
    }

    @Transactional()
    public void deleteCategoria(Long id) {
        Categoria entity = repository.findByIdAndAtivaTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );
        entity.setAtiva(false);
    }
}
