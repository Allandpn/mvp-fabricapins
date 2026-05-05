package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.config.security.SecurityService;
import com.finalphase.fabricapins.ecommerce.domain.entities.Cliente;
import com.finalphase.fabricapins.ecommerce.domain.entities.Endereco;
import com.finalphase.fabricapins.ecommerce.dto.cliente.ClienteMinDTO;
import com.finalphase.fabricapins.ecommerce.dto.cliente.ClienteRequest;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoDTO;
import com.finalphase.fabricapins.ecommerce.dto.endereco.EnderecoPedidoRequest;
import com.finalphase.fabricapins.ecommerce.exception.BusinessException;
import com.finalphase.fabricapins.ecommerce.exception.DatabaseException;
import com.finalphase.fabricapins.ecommerce.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.ecommerce.mapper.ClienteMapper;
import com.finalphase.fabricapins.ecommerce.mapper.EnderecoMapper;
import com.finalphase.fabricapins.ecommerce.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    private EnderecoService enderecoService;
    @Autowired
    private EnderecoMapper enderecoMapper;


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
        validaEnderecoPrincipalUnico(request.enderecos());
        Cliente entity = mapper.toEntity(request);
        for(EnderecoPedidoRequest enderecoRequest : request.enderecos()){
            Endereco endereco = enderecoMapper.toEntity(enderecoRequest);
            endereco.setCliente(entity);
            entity.addEndereco(endereco);
        }
        entity.setAtivo(true);
        try {
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
    private void validaEnderecoPrincipalUnico(List<EnderecoPedidoRequest> enderecos){
        long quantidade = enderecos.stream().filter(EnderecoPedidoRequest::enderecoPrincipal).count();
        if(quantidade != 1){
            throw new BusinessException("Deve existir exatamente um endereço principal");
        }
    }

}
