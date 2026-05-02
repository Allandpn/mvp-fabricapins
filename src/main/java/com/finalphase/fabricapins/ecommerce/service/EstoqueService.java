package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.ItemPedido;
import com.finalphase.fabricapins.ecommerce.exception.InsufficientStockException;
import com.finalphase.fabricapins.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public void reservarEstoque(List<ItemPedido> items){
        for(ItemPedido item : items){
            int updated = produtoRepository.reduzirEstoque(
                    item.getProduto().getId(),
                    item.getQuantidade()
            );
            if(updated == 0){
                throw new InsufficientStockException(
                        "Estoque insuficiente para o produto: " + item.getProduto().getNome()
                );
            }
        }
    }

    public void devolverEstoque(List<ItemPedido> items){
        for(ItemPedido item : items){
            produtoRepository.aumentarEstoque(
                    item.getProduto().getId(),
                    item.getQuantidade()
            );
        }
    }
}
