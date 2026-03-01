package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.Perfil;
import com.finalphase.fabricapins.domain.entities.Usuario;
import com.finalphase.fabricapins.dto.usuario.UsuarioDTO;
import com.finalphase.fabricapins.dto.usuario.UsuarioRequest;
import com.finalphase.fabricapins.exception.DatabaseException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.mapper.UsuarioMapper;
import com.finalphase.fabricapins.repository.PerfilRepository;
import com.finalphase.fabricapins.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private PerfilRepository perfilRepository;
    @Autowired
    private UsuarioMapper mapper;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public UsuarioDTO findById(Long id){
        Usuario entity = repository.searchWithPerfil(id);
        if(entity == null){
            throw new ResourceNotFoundException("Usuario não encontrado");
        }
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        List<Usuario> result = repository.searchAll();
        return result.stream().map(x -> mapper.toDTO(x)).toList();
    }

    @Transactional()
    public UsuarioDTO insertUsuario(@Valid UsuarioRequest request) {

        if(repository.existsByUsername(request.username())){
            throw new DatabaseException("Já existe um usuário com esse nome");
        }
        // Salva password criptografado
        String encryptedPassword = passwordEncoder.encode(request.password());

        Usuario entity = new Usuario(request.username(), encryptedPassword);

        List<Perfil> perfis = perfilRepository.searchAllByName(request.perfis());
        if(perfis.size() != request.perfis().size()){
            throw new ResourceNotFoundException("Nao foi possivel adicionar o Usuario. Algum perfil informado não existe");
        }
        entity.addPerfis(perfis);
        entity = repository.save(entity);
        return mapper.toDTO(entity);
    }

    @Transactional
    public UsuarioDTO updateUsuario(@Valid Long id, UsuarioRequest request) {
        Usuario entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario não encontrado")
        );

        if(request.username() != null && repository.existsByUsernameAndIdNot(request.username(), id)){
            throw new DatabaseException("Já existe um usuário com esse nome");
        }

        mapper.updateFromDto(request, entity);

        if(request.perfis() != null ){
            List<Perfil> perfis = perfilRepository.searchAllByName(request.perfis());
            if(perfis.size() != request.perfis().size()){
                throw new ResourceNotFoundException("Nao foi possivel atualizar o Usuario. Algum perfil informado não existe");
            }

            entity.getPerfis().clear();
            entity.addPerfis(perfis);
        }

        return mapper.toDTO(entity);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        Usuario entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario não encontrado")
        );
        try {
            repository.delete(entity);
            repository.flush();
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Não é possível excluir pois há Cliente ou Perfis associados");
        }
    }

}
