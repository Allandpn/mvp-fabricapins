package com.finalphase.fabricapins.ecommerce.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    Optional<Endereco> findByIdAndClienteId(Long id, Long clienteId);

    Optional<Endereco> findByClienteIdAndEnderecoPrincipalIsTrue(Long clienteId);

    List<Endereco> findByClienteId(Long clienteId);
}
