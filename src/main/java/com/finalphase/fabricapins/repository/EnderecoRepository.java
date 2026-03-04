package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Endereco;
import com.finalphase.fabricapins.dto.endereco.EnderecoDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findByIdAndClienteId(Long id, Long clienteId);

    Optional<List<Endereco>> findByClienteId(Long clienteId);
}
