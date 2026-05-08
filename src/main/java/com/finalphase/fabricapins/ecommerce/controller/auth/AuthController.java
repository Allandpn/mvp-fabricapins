package com.finalphase.fabricapins.ecommerce.controller.auth;

import com.finalphase.fabricapins.ecommerce.domain.entities.RefreshToken;
import com.finalphase.fabricapins.ecommerce.domain.entities.Usuario;
import com.finalphase.fabricapins.ecommerce.dto.auth.RefreshTokenDTO;
import com.finalphase.fabricapins.ecommerce.dto.usuario.UsuarioDTO;
import com.finalphase.fabricapins.ecommerce.repository.RefreshTokenRepository;
import com.finalphase.fabricapins.ecommerce.service.AuthService;
import com.finalphase.fabricapins.ecommerce.service.RefreshTokenService;
import com.finalphase.fabricapins.ecommerce.service.UsuarioService;
import com.finalphase.fabricapins.security.CustomUserDetails;
import com.finalphase.fabricapins.security.CustomUserDetailsService;
import com.finalphase.fabricapins.security.JwtService;
import com.finalphase.fabricapins.ecommerce.dto.auth.LoginRequestDTO;
import com.finalphase.fabricapins.ecommerce.dto.auth.LoginResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Operação de autenticação")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        Usuario usuario = ((CustomUserDetails) userDetails).getUsuario();

        refreshTokenService.create(usuario, refreshToken, jwtService.getRefreshExpirationInstant());
        return new LoginResponseDTO(accessToken, refreshToken);
    }


    @PostMapping("/refresh")
    public LoginResponseDTO refresh(@RequestBody RefreshTokenDTO request){
        return authService.refresh(request);
    }


@PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenDTO request){

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByToken(request.refreshToken()).orElseThrow(
                () -> new RuntimeException("Refresh token inválido")
        );
        refreshTokenService.revoke(refreshTokenEntity);

        return ResponseEntity.noContent().build();
    }
}
