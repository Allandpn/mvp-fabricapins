package com.finalphase.fabricapins.config;

import com.finalphase.fabricapins.domain.entities.Perfil;
import com.finalphase.fabricapins.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PerfilRepository perfilRepository;

    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            if(!perfilRepository.existsByNome(Perfil.ADMIN)){
                perfilRepository.save(new Perfil(Perfil.ADMIN));
            }
            if(!perfilRepository.existsByNome(Perfil.GERENTE)){
                perfilRepository.save(new Perfil(Perfil.GERENTE));
            }
            if(!perfilRepository.existsByNome(Perfil.VENDEDOR)){
                perfilRepository.save(new Perfil(Perfil.VENDEDOR));
            }
            if(!perfilRepository.existsByNome(Perfil.CLIENTE)){
                perfilRepository.save(new Perfil(Perfil.CLIENTE));
            }
        };
    }
}
