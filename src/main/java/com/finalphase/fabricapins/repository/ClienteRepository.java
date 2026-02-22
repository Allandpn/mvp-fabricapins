package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
