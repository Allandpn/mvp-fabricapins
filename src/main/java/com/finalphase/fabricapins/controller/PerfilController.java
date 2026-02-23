package com.finalphase.fabricapins.controller;

import com.finalphase.fabricapins.dto.perfil.PerfilDTO;
import com.finalphase.fabricapins.dto.perfil.PerfilRequest;
import com.finalphase.fabricapins.service.PerfilService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/perfil")
public class PerfilController {

    @Autowired
    private PerfilService service;

    @GetMapping(value = "/{id}")
    public ResponseEntity<PerfilDTO> findPerfilById(@PathVariable Long id){
        PerfilDTO dto = service.findPerfilById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping()
    public ResponseEntity<Page<PerfilDTO>> findAllPerfil(Pageable pageable){
        Page<PerfilDTO> ListDto = service.findAllPerfil(pageable);
        return ResponseEntity.ok(ListDto);
    }

    @PostMapping
    public ResponseEntity<PerfilDTO> insertPerfil(@Valid @RequestBody PerfilRequest request){
        PerfilDTO dto = service.insertPerfil(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<PerfilDTO> updatePerfil(@Valid @PathVariable Long id, @RequestBody PerfilRequest request){
        return ResponseEntity.ok(service.updatePerfil(id, request));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePerfil(@PathVariable Long id){
        service.deletePerfil(id);
        return ResponseEntity.noContent().build();
    }

}
