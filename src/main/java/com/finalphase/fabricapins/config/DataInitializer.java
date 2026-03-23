package com.finalphase.fabricapins.config;

import com.finalphase.fabricapins.domain.entities.Perfil;
import com.finalphase.fabricapins.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PerfilRepository perfilRepository;

    @Bean
    public CommandLineRunner initRoles() {
        return args -> {

            List<String> perfis = List.of(
                    Perfil.ADMIN,
                    Perfil.GERENTE,
                    Perfil.VENDEDOR,
                    Perfil.CLIENTE
            );

            List<String> existentes = perfilRepository.findAll()
                    .stream()
                    .map(Perfil::getNome)
                    .toList();

            List<Perfil> novos = perfis.stream()
                    .filter(p -> !existentes.contains(p))
                    .map(Perfil::new)
                    .toList();

            if (!novos.isEmpty()) {
                perfilRepository.saveAll(novos);
            }
        };
    }
}
