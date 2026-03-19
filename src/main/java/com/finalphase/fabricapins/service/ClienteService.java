package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.config.security.SecurityService;
import com.finalphase.fabricapins.domain.entities.Cliente;
import com.finalphase.fabricapins.dto.cliente.ClienteMinDTO;
import com.finalphase.fabricapins.dto.cliente.ClienteRequest;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.exception.DatabaseException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.mapper.ClienteMapper;
import com.finalphase.fabricapins.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;
    @Autowired
    private ClienteMapper mapper;
    @Autowired
    private SecurityService securityService;


    // TODO - REVISAR
    @Transactional(readOnly = true)
    public ClienteMinDTO findById(Long id) {
        Cliente entity = repository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );
        return mapper.toDTO(entity);
    }

    // TODO - REVISAR
    @Transactional(readOnly = true)
    public Page<ClienteMinDTO> findAll(Pageable pageable) {
        Page<Cliente> result = repository.findAllByAtivoTrue(pageable);
        return result.map(x -> mapper.toDTO(x));
    }

    // TODO - REVISAR
    @Transactional()
    public ClienteMinDTO insertCliente(ClienteRequest request) {
        if(repository.existsByNumeroDocumento(request.numeroDocumento())){
            throw new DatabaseException("Já existe um cliente com esse numero de documento");
        }
        if(repository.existsByEmail(request.email())){
            throw new DatabaseException("Já existe um cliente com esse email");
        }
        Cliente entity = new Cliente(request);
        try {
            entity.setAtivo(true);
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível cadastrar o Cliente");
        }
        return mapper.toDTO(entity);
    }

    // TODO - REVISAR
    @Transactional()
    public ClienteMinDTO updateCliente(Long id, ClienteRequest request) {

        Cliente entity = repository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );
        //valida se cliente é dono do recurso ou é admin
        securityService.validateSelfOrAdmin(entity.getUsuario().getUsername());

        if(repository.existsByNumeroDocumentoAndIdNot(request.numeroDocumento(), id)){
            throw new DatabaseException("Já existe um cliente com esse numero de documento");
        }
        if(repository.existsByEmailAndIdNot(request.email(), id)){
            throw new DatabaseException("Já existe um cliente com esse numero de email");
        }
        mapper.updateFromDto(request, entity);
        return mapper.toDTO(entity);
    }

    // TODO - REVISAR
    @Transactional()
    public void deleteCliente(Long id) {
        Cliente entity = repository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );
        //valida se cliente é dono do recurso ou é admin
        securityService.validateSelfOrAdmin(entity.getUsuario().getUsername());

        entity.setAtivo(false);
    }



    // VALIDADORES
    private boolean validaEnderecoPrincipalUnico(List<EnderecoDTO> enderecos){
        long quantidade = enderecos.stream().filter(EnderecoDTO::enderecoPrincipal).count();
        return quantidade == 1;

    }
}
