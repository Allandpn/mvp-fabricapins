package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.RefreshToken;
import com.finalphase.fabricapins.ecommerce.domain.entities.Usuario;
import com.finalphase.fabricapins.ecommerce.dto.auth.LoginRequestDTO;
import com.finalphase.fabricapins.ecommerce.dto.auth.LoginResponseDTO;
import com.finalphase.fabricapins.ecommerce.dto.auth.RefreshTokenDTO;
import com.finalphase.fabricapins.ecommerce.repository.RefreshTokenRepository;
import com.finalphase.fabricapins.security.CustomUserDetails;
import com.finalphase.fabricapins.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Transactional
    public LoginResponseDTO refresh(RefreshTokenDTO request){
        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(request.refreshToken());

        if (!refreshTokenService.isValid(refreshTokenEntity)){
            throw new RuntimeException("Refresh token inválido");
        }

        Usuario usuario = refreshTokenEntity.getUsuario();

        if(!usuario.isAtivo()){
            throw new RuntimeException("Usuário desativado");
        }

        UserDetails userDetails = new CustomUserDetails(usuario);

        if(!jwtService.isRefreshToken(request.refreshToken())){
            throw new RuntimeException("Token inválido");
        }

        String newAccessToken = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(newAccessToken, request.refreshToken());
    }


}
