package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.Produto;
import com.finalphase.fabricapins.domain.entities.ProdutoVariacao;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.dto.produto_variacao.CatalogoProdutoVariacaoDTO;
import com.finalphase.fabricapins.dto.produto_variacao.ProdutoVariacaoDTO;
import com.finalphase.fabricapins.dto.produto_variacao.ProdutoVariacaoRequest;
import com.finalphase.fabricapins.exception.BusinessException;
import com.finalphase.fabricapins.exception.DatabaseException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.mapper.ProdutoVariacaoMapper;
import com.finalphase.fabricapins.repository.ProdutoRepository;
import com.finalphase.fabricapins.repository.ProdutoVariacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProdutoVariacaoService {

    @Autowired
    private ProdutoVariacaoRepository produtoVariacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoVariacaoMapper mapper;

    @Transactional(readOnly = true)
    public ProdutoVariacaoDTO findById(Long produtoId, Long variacaoId) {
        ProdutoVariacao entity = produtoVariacaoRepository
                .findByProdutoIdAndIdAndProdutoAtivoTrueAndAtivoTrue(produtoId, variacaoId).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado")
        );
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<CatalogoProdutoVariacaoDTO> findAll(Long produtoId) {
        List<ProdutoVariacao> entity = produtoVariacaoRepository
                .findAllByProdutoIdAndProdutoAtivoTrueAndAtivoTrue(produtoId).orElseThrow(
                        () -> new ResourceNotFoundException("Produto não encontrado")
                );
        return entity.stream().map(mapper::toCatalogoDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProdutoVariacao> buscarProdutos(List<ItemPedidoRequest> items){
        Set<Long> idsPedido = new HashSet<>();
        for(ItemPedidoRequest item : items) {
            if (!idsPedido.add(item.produtoVariacaoId())) {
                throw new BusinessException("Produto duplicado");
            }
        }
        List<Long> ids = items.stream().map(ItemPedidoRequest::produtoVariacaoId).toList();
        List<ProdutoVariacao> listaProdutos = produtoVariacaoRepository.findAllByIdInAndAtivoTrue(ids);
        if(listaProdutos.size() != ids.size()){
            throw new ResourceNotFoundException("Um ou mais produtos não foram encontrados");
        }
        return listaProdutos;
    }


    @Transactional
    public ProdutoVariacaoDTO insertProduto(Long produtoId,ProdutoVariacaoRequest request) {
        Produto produto = produtoRepository.findByIdAndAtivoTrue(produtoId).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado")
        );
        if(produtoVariacaoRepository.existsBySku(request.sku())){
            throw new DatabaseException("Já existe um produto com esse nome");
        }
        ProdutoVariacao entity = mapper.toEntity(request);
        try{
            entity.setAtivo(true);
            entity.setProduto(produto);
            produtoVariacaoRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível cadastrar o Produto");
        }
        return mapper.toDTO(entity);
    }

    @Transactional
    public ProdutoVariacaoDTO updateProduto(Long produtoId, Long variacaoId,ProdutoVariacaoRequest request) {
        ProdutoVariacao entity = produtoVariacaoRepository
                .findByProdutoIdAndIdAndProdutoAtivoTrueAndAtivoTrue(produtoId, variacaoId).orElseThrow(
                        () -> new ResourceNotFoundException("Produto não encontrado")
                );
        if(produtoVariacaoRepository.existsBySkuAndIdNot(request.sku(), variacaoId)){
            throw new DatabaseException("Já existe um produto com esse nome");
        }
        mapper.updateFromDto(request, entity);
        return mapper.toDTO(entity);
    }

    @Transactional
    public void deleteProduto(Long produtoId, Long variacaoId) {
        ProdutoVariacao entity = produtoVariacaoRepository
                .findByProdutoIdAndIdAndProdutoAtivoTrueAndAtivoTrue(produtoId, variacaoId).orElseThrow(
                        () -> new ResourceNotFoundException("Produto não encontrado")
                );
        entity.setAtivo(false);
    }
}
