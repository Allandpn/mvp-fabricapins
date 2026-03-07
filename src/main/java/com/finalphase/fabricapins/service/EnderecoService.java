package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.Cliente;
import com.finalphase.fabricapins.domain.entities.Endereco;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.exception.DatabaseException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.mapper.EnderecoMapper;
import com.finalphase.fabricapins.repository.ClienteRepository;
import com.finalphase.fabricapins.repository.EnderecoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository repository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoMapper mapper;

    @Transactional(readOnly = true)
    public EnderecoDTO findByIdAndClienteId(Long clienteId, Long enderecoId) {
        Endereco entity = repository
                .findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Endereço não encontrado"));

        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<EnderecoDTO> findAllByClienteId(Long clienteId) {
        List<Endereco> result = repository
                .findByClienteId(clienteId);
        return result.stream().map(mapper::toDTO).toList();
    }

    @Transactional()
    public EnderecoDTO insertEndereco(Long clienteId, EnderecoDTO request) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );
        Endereco entity = mapper.toEntity(request);
        try {
            cliente.addEndereco(entity);
            repository.save(entity);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Não foi possível cadastrar o endereço");
        }
        return mapper.toDTO(entity);
    }

    @Transactional()
    public EnderecoDTO updateEndereco(Long clienteId, Long enderecoId, EnderecoDTO request) {
        Endereco entity = repository
                .findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Endereço não encontrado"));
        mapper.updateFromDto(request, entity);
        return mapper.toDTO(entity);
    }

    @Transactional()
    public void deleteEndereco(Long clienteId, Long enderecoId) {
        Endereco entity = repository
                .findByIdAndClienteId(enderecoId, clienteId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Endereço não encontrado"));
        try {
            repository.delete(entity);
            repository.flush();
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Não é possível excluir pois há entidades associadas");
        }
    }
}
