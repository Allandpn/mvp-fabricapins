package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.*;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoMinDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoAdminRequest;
import com.finalphase.fabricapins.exception.BusinessException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.mapper.PedidoMapper;
import com.finalphase.fabricapins.repository.ClienteRepository;
import com.finalphase.fabricapins.repository.PedidoRepository;
import com.finalphase.fabricapins.repository.ProdutoVariacaoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ProdutoVariacaoRepository produtoVariacaoRepository;
    @Autowired
    private ProdutoVariacaoService produtoVariacaoService;
    @Autowired
    private ItemPedidoService itemPedidoService;
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
    public PedidoMinDTO insertPedido(@Valid PedidoAdminRequest request) {
       validaSeListaItensVazia(request);
       Cliente cliente = buscarCliente(request.clienteId());
       TipoCliente tipoCliente = resolverTipoCliente(cliente);
       Pedido pedido = mapper.toEntity(request);
       pedido.setCliente(cliente);
       pedido.setStatusPedido(StatusPedido.AGUARDANDO_PAGAMENTO);
       pedido.setCodigoPedido(pedido.gerarCodigoPedido());
       List<ProdutoVariacao> produtos = produtoVariacaoService.buscarProdutos(request.items());
       List<ItemPedido> itemsPedido =  criarItemPedido(produtos, request.items(), tipoCliente);
       for(ItemPedido item : itemsPedido){
           pedido.adicionarItem(item);
       }
       List<CupomDesconto> cuponsDesconto = buscarCupons(request.cupons());
        for(CupomDesconto cupom : cuponsDesconto){
            cupomDescontoService.validarLimiteUso(cupom);
            pedido.aplicarCupom(cupom);
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

    private List<ItemPedido> criarItemPedido(List<ProdutoVariacao> produtos, List<ItemPedidoRequest> items, TipoCliente tipoCliente) {
        //diminui complexidade para 0n
        Map<Long, ProdutoVariacao> produtosMap = produtos.stream().collect(
                Collectors.toMap(ProdutoVariacao::getId, p -> p));

        List<ItemPedido> listaPedidos = new ArrayList<>();
        for(ItemPedidoRequest item : items) {
          ProdutoVariacao produto = produtosMap.get(item.produtoVariacaoId());
          ItemPedido itemPedido = itemPedidoService.createItemPedido(item, produto, tipoCliente);
            listaPedidos.add(itemPedido);
        }
        return listaPedidos;
    }


    private List<CupomDesconto> buscarCupons(Set<String> cupons) {
        if(cupons == null || cupons.isEmpty()){
            return Collections.emptyList();
        }
        List<CupomDesconto> listaCupons = new ArrayList<>();
        for(String codigo : cupons){
            CupomDesconto cupom = cupomDescontoService.findByCodigo(codigo);
            listaCupons.add(cupom);
        }
        return listaCupons;
    }


    // Validadores
    private void validaSeListaItensVazia(PedidoAdminRequest request){
        if(request.items() == null || request.items().isEmpty()){
            throw new BusinessException("Pedido deve possui no mínimo um item");
        }
    }


    public PedidoMinDTO alterarStatusPedido(@Valid PedidoAdminRequest request) {
        return null;
    }
}

