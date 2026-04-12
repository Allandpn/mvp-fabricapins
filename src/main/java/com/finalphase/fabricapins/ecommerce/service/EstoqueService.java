package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.ItemPedido;
import com.finalphase.fabricapins.ecommerce.exception.InsufficientStockException;
import com.finalphase.fabricapins.ecommerce.repository.ProdutoVariacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    @Autowired
    private ProdutoVariacaoRepository produtoVariacaoRepository;

    public void reservarEstoque(List<ItemPedido> items){
        for(ItemPedido item : items){
            int updated = produtoVariacaoRepository.reduzirEstoque(
                    item.getProdutoVariacao().getId(),
                    item.getQuantidade()
            );
            if(updated == 0){
                throw new InsufficientStockException(
                        "Estoque insuficiente para o produto: " + item.getProdutoVariacao().getNome()
                );
            }
        }
    }

    public void devolverEstoque(List<ItemPedido> items){
        for(ItemPedido item : items){
            produtoVariacaoRepository.aumentarEstoque(
                    item.getProdutoVariacao().getId(),
                    item.getQuantidade()
            );
        }
    }
}
