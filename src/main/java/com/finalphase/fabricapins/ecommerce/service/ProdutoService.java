package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.Categoria;
import com.finalphase.fabricapins.ecommerce.domain.entities.Produto;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoMinDTO;
import com.finalphase.fabricapins.ecommerce.dto.produto.ProdutoRequest;
import com.finalphase.fabricapins.ecommerce.exception.BusinessException;
import com.finalphase.fabricapins.ecommerce.exception.DatabaseException;
import com.finalphase.fabricapins.ecommerce.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.ecommerce.mapper.ProdutoMapper;
import com.finalphase.fabricapins.ecommerce.repository.CategoriaRepository;
import com.finalphase.fabricapins.ecommerce.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Sort sort = Sort.by(Sort.Order.desc("ativo"));
        if (pageable.getSort().isSorted()) {
            sort = sort.and(pageable.getSort());
        }
        Pageable pageableComAtivoPrimeiro = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );
        Page<Produto> entity = produtoRepository.findAll(pageableComAtivoPrimeiro);
        return entity.map(mapper::toMinDTO);
    }

    @Transactional
    public ProdutoMinDTO insertProduto(@Valid ProdutoRequest request) {
        Produto entity = mapper.toEntity(request);
        if(produtoRepository.existsBySku(request.sku())){
            throw new DatabaseException("Já existe um produto com esse nome");
        }

        Categoria categoria = categoriaRepository.findByIdAndAtivaTrue(request.categoriaId()).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );
        try {
            entity.setCategoria(categoria);
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
        if(produtoRepository.existsBySkuAndIdNot(request.sku(), id)){
            throw new DatabaseException("Já existe um produto com esse nome");
        }
        Categoria categoria = categoriaRepository.findByIdAndAtivaTrue(request.categoriaId()).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );
        entity.setCategoria(categoria);
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
    @Transactional(readOnly = true)
    public List<Produto> buscarProdutos(List<ItemPedidoRequest> items){
        Set<Long> idsPedido = new HashSet<>();
        for(ItemPedidoRequest item : items) {
            if (!idsPedido.add(item.id())) {
                throw new BusinessException("Produto duplicado");
            }
        }
        List<Long> ids = items.stream().map(ItemPedidoRequest::id).toList();
        List<Produto> listaProdutos = produtoRepository.findAllByIdInAndAtivoTrue(ids);
        if(listaProdutos.size() != ids.size()){
            throw new ResourceNotFoundException("Um ou mais produtos não foram encontrados");
        }
        return listaProdutos;
    }

}
