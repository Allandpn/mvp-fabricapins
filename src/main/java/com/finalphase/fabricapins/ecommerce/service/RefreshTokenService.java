package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.RefreshToken;
import com.finalphase.fabricapins.ecommerce.domain.entities.Usuario;
import com.finalphase.fabricapins.ecommerce.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Transactional
    public RefreshToken create(Usuario usuario, String token, Instant expiraEm){
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .usuario(usuario)
                .expiraEm(expiraEm)
                .revoked(false)
                .build();
        return repository.save(refreshToken);
    }

    @Transactional
    public void revoke(RefreshToken token){
        token.setRevoked(true);
        repository.save(token);
    }

    public boolean isValid(RefreshToken token){
        return !token.isRevoked() && token.getExpiraEm().isAfter(Instant.now());
    }

    @Transactional
    public RefreshToken findByToken(String refreshToken) {
        return repository.findByToken(refreshToken).orElseThrow(
                () -> new RuntimeException("Refresh token não encontrado")
        );
    }
}
