package com.finalphase.fabricapins.ecommerce.repository;

import com.finalphase.fabricapins.ecommerce.domain.entities.RefreshToken;
import com.finalphase.fabricapins.ecommerce.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository  extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}
