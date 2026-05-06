package com.finalphase.fabricapins.ecommerce.controller.auth;

import com.finalphase.fabricapins.config.ApiPaths;
import com.finalphase.fabricapins.config.security.CustomUserDetailsService;
import com.finalphase.fabricapins.config.security.JwtService;
import com.finalphase.fabricapins.ecommerce.dto.auth.LoginRequestDTO;
import com.finalphase.fabricapins.ecommerce.dto.auth.LoginResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.API + "/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());

        String jwt = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(jwt);
    }
}
