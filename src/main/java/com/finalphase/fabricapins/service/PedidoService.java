package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.*;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoMinDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoRequest;
import com.finalphase.fabricapins.exception.InsufficientStockException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.mapper.PedidoMapper;
import com.finalphase.fabricapins.repository.ClienteRepository;
import com.finalphase.fabricapins.repository.PedidoRepository;
import com.finalphase.fabricapins.repository.ProdutoVariacaoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.InsufficientResourcesException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ItemPedidoService itemPedidoService;
    @Autowired
    private ProdutoVariacaoRepository produtoVariacaoRepository;
    @Autowired
    private CupomDescontoService cupomDescontoService;

    @Autowired
    private PedidoMapper mapper;


    @Transactional(readOnly = true)
    public PedidoDTO findById(Long id) {
        Pedido entity = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public PedidoDTO findByCodigo(String codigo) {
        Pedido entity = pedidoRepository.findByCodigoPedido(codigo).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<PedidoMinDTO> findAll(Pageable pageable) {
        Page<Pedido> result = pedidoRepository.findAll(pageable);
        return result.map(mapper::toMinDTO);
    }

    @Transactional()
    public PedidoMinDTO insertPedido(@Valid PedidoRequest request) {
       Cliente cliente = buscarCliente(request.clienteId());
       TipoCliente tipoCliente = resolverTipoCliente(cliente);
       Pedido pedido = mapper.toEntity(request);
       pedido.setCliente(cliente);
       pedido.setStatusPedido(StatusPedido.AGUARDANDO_PAGAMENTO);
       List<ProdutoVariacao> produtos = buscarProdutos(request.items());
       List<ItemPedido> itemsPedido =  criarItemPedido(produtos, request.items(), tipoCliente);
       List< CupomDesconto> cuponsDesconto = buscarCupons(request.cupons());
       for(ItemPedido item : itemsPedido){
           pedido.adicionarItem(item);
       }
       pedido = pedidoRepository.save(pedido);
       return mapper.toMinDTO(pedido);
    }



    private Cliente buscarCliente(Long id) {
        if(id == null){
            return null;
        }
        return clienteRepository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );
    }
    private TipoCliente resolverTipoCliente(Cliente cliente) {
        if(cliente == null){
            return TipoCliente.VAREJO;
        }
        return cliente.getTipoCliente();
    }

    private List<ProdutoVariacao> buscarProdutos(List<ItemPedidoRequest> items){
        List<ProdutoVariacao> produtos = new ArrayList<>();
        for(ItemPedidoRequest item : items){
            ProdutoVariacao produtoVariacao = produtoVariacaoRepository
                    .findByIdAndAtivoTrue(item.produtoVariacaoId()).orElseThrow(
                            () -> new ResourceNotFoundException("Produto não encontrado")
                    );
            produtos.add(produtoVariacao);
        }
        return produtos;
    }

    private List<ItemPedido> criarItemPedido(List<ProdutoVariacao> produtos, List<ItemPedidoRequest> items, TipoCliente tipoCliente) {
        List<ItemPedido> listaPedidos = new ArrayList<>();
        for(ItemPedidoRequest item : items) {
          ProdutoVariacao produto = (ProdutoVariacao) produtos.stream().filter(x -> x.getId().equals(item.produtoVariacaoId()));
            ItemPedido itemPedido = itemPedidoService.createItemPedido(item, produto, tipoCliente);
            listaPedidos.add(itemPedido);
        }
        return listaPedidos;
    }


    private List<CupomDesconto> buscarCupons(Set<String> cupons){
        List<CupomDesconto> listaCupons = new ArrayList<>();
        cupons.stream().map(listaCupons.add(x -> cupomDescontoService.findByCodigo(x))).map(x -> listaCupons.add(x));
        }
    }




}

