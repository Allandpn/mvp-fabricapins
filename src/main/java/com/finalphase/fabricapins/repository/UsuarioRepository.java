package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
