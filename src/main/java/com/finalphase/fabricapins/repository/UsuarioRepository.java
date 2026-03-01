package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Usuario;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT DISTINCT u FROM Usuario u " +
            "JOIN FETCH u.perfis " +
            "WHERE u.id = :id")
    Usuario searchWithPerfil(@Param("id") Long id);


    @Query(value = "SELECT DISTINCT u FROM Usuario u " +
            "JOIN FETCH u.perfis ")
    List<Usuario> searchAll();

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);
}
