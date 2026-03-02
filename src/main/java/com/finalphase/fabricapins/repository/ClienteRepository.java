package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Cliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByNumeroDocumento(String s);

    boolean existsByEmail(String email);
}
