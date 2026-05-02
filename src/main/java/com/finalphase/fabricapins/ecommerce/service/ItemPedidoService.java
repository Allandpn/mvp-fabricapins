package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.ItemPedido;
import com.finalphase.fabricapins.ecommerce.domain.entities.Produto;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.ecommerce.mapper.ItemPedidoMapper;
import com.finalphase.fabricapins.ecommerce.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ItemPedidoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ItemPedidoMapper mapper;


    @Transactional
    public ItemPedido createItemPedido(ItemPedidoRequest dto, Produto produto, TipoCliente tipoCliente){
        BigDecimal precoUnitario = tipoCliente == TipoCliente.VAREJO ? produto.getPrecoVarejo() : produto.getPrecoRevenda();
        String nomeProdutoSnapshot = produto.getNome();
        String imgProdutoSnapshot = produto.getImgUrl();
        BigDecimal custoUnitarioSnapshot = produto.getCustoProducao();
        ItemPedido itemPedido = new ItemPedido(dto.quantidade(), precoUnitario, nomeProdutoSnapshot, custoUnitarioSnapshot, imgProdutoSnapshot);
        itemPedido.setProduto(produto);
        return itemPedido;
    }
}
