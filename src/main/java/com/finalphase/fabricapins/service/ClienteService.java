package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.Cliente;
import com.finalphase.fabricapins.domain.entities.Endereco;
import com.finalphase.fabricapins.dto.cliente.ClienteMinDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteRequest;
import com.finalphase.fabricapins.dto.cliente.ClienteWtihPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.exception.DatabaseException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.mapper.ClienteMapper;
import com.finalphase.fabricapins.mapper.EnderecoMapper;
import com.finalphase.fabricapins.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Autowired
    private ClienteMapper mapper;


    // TODO - REVISAR
    @Transactional(readOnly = true)
    public ClienteWtihPedidoDTO findById(Long id) {
        Cliente entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario não encontrado")
        );
        return mapper.toDTOWithPedido(entity);
    }

    // TODO - REVISAR
    @Transactional(readOnly = true)
    public List<ClienteMinDTO> findAll() {
        List<Cliente> result = repository.findAll();
        return result.stream().map(x -> mapper.toDTO(x)).toList();
    }

    // TODO - REVISAR
    @Transactional()
    public ClienteMinDTO insertCliente(@Valid ClienteRequest request) {
        if(repository.existsByNumeroDocumento(request.numeroDocumento())){
            throw new DatabaseException("Já existe um cliente com esse numero de documento");
        }
        if(repository.existsByEmail(request.email())){
            throw new DatabaseException("Já existe um cliente com esse numero de documento");
        }
        if(!validaEnderecoPrincipalUnico(request.enderecos())){
            throw new DatabaseException("Deve existir apenas um endereço principal");
        }
        Cliente entity = mapper.toEntity(request);
        List<Endereco> enderecos = new ArrayList<>(entity.getEnderecos());
        entity.getEnderecos().clear();
        enderecos.forEach(entity::addEndereco);
        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("CPF ou Email já cadastrados");
        }
        return mapper.toDTO(entity);
    }

    // TODO - REVISAR
    @Transactional()
    public ClienteMinDTO updateCliente(Long id, @Valid ClienteRequest request) {
        Cliente entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );

        if(repository.existsByNumeroDocumento(request.numeroDocumento())){
            throw new DatabaseException("Já existe um cliente com esse numero de documento");
        }
        if(repository.existsByEmail(request.email())){
            throw new DatabaseException("Já existe um cliente com esse numero de documento");
        }

        // TODO - ERRO AO ATUALIZAR ENDERECOS
        if(request.enderecos() != null){
            List<Endereco> enderecos = new ArrayList<>(entity.getEnderecos());
            entity.getEnderecos().clear();
            enderecos.forEach(entity::addEndereco);
        }
        mapper.updateFromDto(request, entity);
        return mapper.toDTO(entity);
    }

    // TODO - REVISAR
    @Transactional()
    public void deleteCliente(Long id) {
        Cliente entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );
        try {
            repository.delete(entity);
            repository.flush();
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Não é possível excluir pois há entidades associadas");
        }
    }



    // VALIDADORES
    private boolean validaEnderecoPrincipalUnico(List<EnderecoDTO> enderecos){
        long quantidade = enderecos.stream().filter(EnderecoDTO::enderecoPrincipal).count();
        return quantidade == 1;

    }
}
