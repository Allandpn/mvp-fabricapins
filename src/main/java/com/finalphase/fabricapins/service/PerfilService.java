package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.Perfil;
import com.finalphase.fabricapins.dto.perfil.PerfilMinDTO;
import com.finalphase.fabricapins.dto.perfil.PerfilRequest;
import com.finalphase.fabricapins.dto.perfil.PerfilWithUsuariosDTO;
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

import java.util.List;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository repository;

    @Autowired
    private PerfilMapper mapper;

    @Transactional(readOnly = true)
    public PerfilMinDTO findById(Long id){
        Perfil entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Perfil não encontrado")
        );
        return mapper.toMinDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<PerfilMinDTO> findAll(){
        List<Perfil> result = repository.findAll();
        return result.stream().map(x -> mapper.toMinDTO(x)).toList();
    }

    @Transactional(readOnly = true)
    public List<PerfilWithUsuariosDTO> findAllWithUsuarios(){
        List<Perfil> result = repository.searchAllWithUsuarios();
        return result.stream().map(x -> mapper.toWithUsersDTO(x)).toList();
    }

    @Transactional
    public PerfilMinDTO insertPerfil(PerfilRequest request){
        Perfil entity = mapper.toEntity(request);
        try{
        entity = repository.save(entity);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Já existe um perfil com esse nome");
        }
        return mapper.toMinDTO(entity);
    }

    @Transactional
    public PerfilMinDTO updatePerfil(Long id, PerfilRequest request){
        Perfil entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Perfil não encontrado")
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
                () -> new ResourceNotFoundException("Perfil não encontrado")
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
