package com.finalphase.fabricapins.ecommerce.service;

import com.finalphase.fabricapins.ecommerce.domain.entities.ItemPedido;
import com.finalphase.fabricapins.ecommerce.domain.entities.ProdutoVariacao;
import com.finalphase.fabricapins.ecommerce.domain.enums.TipoCliente;
import com.finalphase.fabricapins.ecommerce.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.ecommerce.mapper.ItemPedidoMapper;
import com.finalphase.fabricapins.ecommerce.repository.ProdutoVariacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ItemPedidoService {

    @Autowired
    private ProdutoVariacaoRepository produtoVariacaoRepository;
    @Autowired
    private ItemPedidoMapper mapper;


    @Transactional
    public ItemPedido createItemPedido(ItemPedidoRequest dto, ProdutoVariacao produtoVariacao, TipoCliente tipoCliente){
        BigDecimal precoUnitario = tipoCliente == TipoCliente.VAREJO ? produtoVariacao.getPrecoVarejo() : produtoVariacao.getPrecoRevenda();
        String nomeProdutoSnapshot = produtoVariacao.getNome();
        String imgProdutoSnapshot = produtoVariacao.getImgUrl();
        BigDecimal custoUnitarioSnapshot = produtoVariacao.getCustoProducao();
        ItemPedido itemPedido = new ItemPedido(dto.quantidade(), precoUnitario, nomeProdutoSnapshot, custoUnitarioSnapshot, imgProdutoSnapshot);
        itemPedido.setProdutoVariacao(produtoVariacao);
        return itemPedido;
    }
}
