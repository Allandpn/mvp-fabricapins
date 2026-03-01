package com.finalphase.fabricapins.repository;

import com.finalphase.fabricapins.domain.entities.Perfil;
import com.finalphase.fabricapins.dto.perfil.PerfilWithUsuariosDTO;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    @Query(value = "SELECT DISTINCT p FROM Perfil p " +
            "LEFT JOIN FETCH p.usuarios")
    List<Perfil> searchAllWithUsuarios();

}
