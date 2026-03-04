package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);

    @Query(value = "SELECT c FROM Cliente c")
    List<Cliente> searchAll(Pageable pageable);

    boolean existsByNumeroDocumentoAndIdNot(String s, Long id);

    boolean existsByNumeroDocumento(String s);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Cliente> findByIdAndAtivoTrue(Long id);

    Page<Cliente> findAllByAtivoTrue(Pageable pageable);
}
