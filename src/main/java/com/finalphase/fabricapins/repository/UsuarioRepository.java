package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Usuario;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT DISTINCT u FROM Usuario u " +
            "JOIN FETCH u.perfis " +
            "WHERE u.id = :id " +
            "AND u.ativo = true")
    Usuario searchWithPerfil(@Param("id") Long id);


    @Query(value = "SELECT DISTINCT u FROM Usuario u " +
            "JOIN FETCH u.perfis " +
            "WHERE u.ativo = true")
    List<Usuario> searchAll();

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);

    Optional<Usuario> findByIdAndAtivoTrue(@Valid Long id);


    @EntityGraph(attributePaths = "perfis") // ja retorna perfis do banco para o SpringSecurity
    Optional<Usuario> findByUsername(String username);
}
