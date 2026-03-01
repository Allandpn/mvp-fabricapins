package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.Perfil;
import com.finalphase.fabricapins.dto.perfil.PerfilMinDTO;
import com.finalphase.fabricapins.dto.perfil.PerfilRequest;
import com.finalphase.fabricapins.exception.DatabaseException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.mapper.PerfilMapper;
import com.finalphase.fabricapins.repository.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository repository;

    @Autowired
    private PerfilMapper mapper;

    @Transactional(readOnly = true)
    public PerfilMinDTO findById(Long id){
        Perfil entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        return mapper.toMinDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<PerfilMinDTO> findAll(Pageable pageable){
        Page<Perfil> result = repository.findAll(pageable);
        return result.map(x -> mapper.toMinDTO(x));
    }

    @Transactional
    public PerfilMinDTO insertPerfil(PerfilRequest request){
        Perfil entity = mapper.toEntity(request);
        entity = repository.save(entity);
        return mapper.toMinDTO(entity);
    }

    @Transactional
    public PerfilMinDTO updatePerfil(Long id, PerfilRequest request){
        Perfil entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        try {
            mapper.updateFromDto(request, entity);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Já existe um perfil com esse nome");
        }
        return mapper.toMinDTO(entity);
    }

    @Transactional
    public void deletePerfil(Long id){
        Perfil entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado")
        );
        try {
            repository.delete(entity);
            repository.flush();
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Não é possível excluir pois há Usuarios associados");
        }
    }

}
