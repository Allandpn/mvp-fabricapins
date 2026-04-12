package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.Categoria;
import com.finalphase.fabricapins.ecommerce.domain.entities.Produto;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoMinDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoRequest;
import com.finalphase.fabricapins.ecommerce.exception.DatabaseException;
import com.finalphase.fabricapins.ecommerce.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.ecommerce.mapper.ProdutoMapper;
import com.finalphase.fabricapins.ecommerce.repository.CategoriaRepository;
import com.finalphase.fabricapins.ecommerce.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProdutoMapper mapper;

    @Transactional(readOnly = true)
    public ProdutoDTO findById(Long id) {
        Produto entity = produtoRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado")
        );
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoMinDTO> findAll(Pageable pageable) {
        Page<Produto> entity = produtoRepository.findAllByAtivoTrue(pageable);
        return entity.map(mapper::toMinDTO);
    }

    @Transactional
    public ProdutoMinDTO insertProduto(@Valid ProdutoRequest request) {
        Produto entity = mapper.toEntity(request);
        String slug = entity.gerarSlug(request.nome());
        if(produtoRepository.existsBySlug(slug)){
            throw new DatabaseException("Já existe um produto com esse nome");
        }

        Categoria categoria = categoriaRepository.findByIdAndAtivaTrue(request.categoriaId()).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );
        try {
            entity.setCategoria(categoria);
            entity.setSlug(slug);
            entity.setAtivo(true);
            produtoRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível cadastrar o Produto");
        }
        return mapper.toMinDTO(entity);
    }

    @Transactional
    public ProdutoMinDTO updateProduto(Long id, @Valid ProdutoRequest request) {
        Produto entity = produtoRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado")
        );
        String slug = entity.gerarSlug(request.nome());
        if(produtoRepository.existsBySlugAndIdNot(slug, id)){
            throw new DatabaseException("Já existe um produto com esse nome");
        }
        Categoria categoria = categoriaRepository.findByIdAndAtivaTrue(request.categoriaId()).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );
        entity.setCategoria(categoria);
        entity.setSlug(slug);
        // TODO - REVISAR UPDATE
        mapper.partialUpdateFromDto(request, entity);
        return mapper.toMinDTO(entity);
    }

    @Transactional
    public void deleteProduto(Long id) {
        Produto entity = produtoRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado")
        );
        entity.setAtivo(false);
    }


    // HELPERS

}
