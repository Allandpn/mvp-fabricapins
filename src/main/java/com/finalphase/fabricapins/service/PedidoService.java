package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.*;
import com.finalphase.fabricapins.domain.enums.FreteProvider;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.dto.cliente.ClienteSnapshot;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoRequest;
import com.finalphase.fabricapins.dto.frete.FreteRequest;
import com.finalphase.fabricapins.dto.frete.OpcaoFreteDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.dto.pedido.PedidoAdminRequest;
import com.finalphase.fabricapins.dto.pedido.PedidoDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoMinDTO;
import com.finalphase.fabricapins.dto.pedido.PedidoRascunhoRequest;
import com.finalphase.fabricapins.exception.BusinessException;
import com.finalphase.fabricapins.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.integration.frete.FreteGateway;
import com.finalphase.fabricapins.integration.frete.FreteGatewayResolver;
import com.finalphase.fabricapins.mapper.EnderecoMapper;
import com.finalphase.fabricapins.mapper.ItemPedidoMapper;
import com.finalphase.fabricapins.mapper.OpcaoFreteMapper;
import com.finalphase.fabricapins.mapper.PedidoMapper;
import com.finalphase.fabricapins.repository.ClienteRepository;
import com.finalphase.fabricapins.repository.EnderecoRepository;
import com.finalphase.fabricapins.repository.PedidoRepository;
import com.finalphase.fabricapins.repository.ProdutoVariacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    EnderecoRepository enderecoRepository;

    @Autowired
    private ProdutoVariacaoService produtoVariacaoService;
    @Autowired
    private ItemPedidoService itemPedidoService;
    @Autowired
    private CupomDescontoService cupomDescontoService;

    @Autowired
    private PedidoMapper mapper;
    @Autowired
    private ItemPedidoMapper itemPedidoMapper;
    @Autowired
    private EnderecoMapper enderecoMapper;
    @Autowired
    private OpcaoFreteMapper opcaoFreteMapper;

    @Autowired
    private FreteGatewayResolver freteGatewayResolver;



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

    // Cria Pedido Completo
    @Transactional()
    public PedidoMinDTO insertPedidoCompleto(PedidoAdminRequest request) {
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

    // Cria pedidos em etapas - Criar rascunho do pedido
    @Transactional()
    public PedidoMinDTO insertPedidoRascunho(PedidoRascunhoRequest request) {
        ClienteSnapshot cliente = resolveCliente(request);
        Pedido pedido = new Pedido(cliente);
        pedido.setCodigoPedido(pedido.gerarCodigoPedido());
        pedido.setOrigemPedido(request.origemPedido());
        pedido.setStatusPedido(StatusPedido.RASCUNHO);
        pedido.setObservacao(request.observacao());
        pedido = pedidoRepository.save(pedido);
        return mapper.toMinDTO(pedido);
    }

    // adicionar item ao pedido
    @Transactional
    public ItemPedidoDTO insertItemPedido(Long pedidoId, ItemPedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        ProdutoVariacao produto = produtoVariacaoRepository.findByIdAndAtivoTrue(request.id()).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrado")
        );
        // adiciona a item existente
        Optional<ItemPedido> itemPedidoExistente = pedido.getItemsPedido()
                .stream()
                .filter(x -> x.getProdutoVariacao().getId().equals(produto.getId())).findFirst();
        if(itemPedidoExistente.isPresent()){
            ItemPedido item = itemPedidoExistente.get();
            pedido.incrementarItem(item, request.quantidade());
            return itemPedidoMapper.toDTO(item);
        }
        // cria novo item
        ItemPedido item = itemPedidoService.createItemPedido(
                request,
                produto,
                pedido.getTipoCliente()
        );
        pedido.adicionarItem(item);
        pedidoRepository.save(pedido);
        return itemPedidoMapper.toDTO(item);
    }


    // adicionar endereco ao pedido
    @Transactional
    public PedidoMinDTO definirEndereco(Long pedidoId, EnderecoPedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        EnderecoPedidoDTO endereco = resolveEndereco(pedido, request);
        pedido.definirEndereco(endereco);
        pedidoRepository.save(pedido);
        return mapper.toMinDTO(pedido);
    }

    // define frete
    @Transactional()
    public PedidoMinDTO definirFrete(Long pedidoId, FreteRequest request){
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        if(pedido.getCep() == null){
            throw new BusinessException("Endereço deve ser informado antes de selecionar frete");
        }
        if(pedido.getItemsPedido().isEmpty()){
            throw new BusinessException("Pedido deve possuir itens para selecionar frete");
        }
        if (pedido.getOpcoesFrete() == null){
            throw new BusinessException("Frete ainda não foi calculado");
        }
        OpcaoFretePedido opcao = pedido.getOpcoesFrete()
                .stream()
                .filter(x -> x.getServiceId().equals(request.serviceId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Opção de frete inválida"));

        pedido.definirFrete(opcao);
        return mapper.toMinDTO(pedido);
    }

    // calcula frete
    @Transactional()
    public List<OpcaoFreteDTO> calcularFrete(Long pedidoId){

        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        if(pedido.getCep() == null){
            throw new BusinessException("Endereço deve ser informado antes de calcular frete");
        }
        if(pedido.getItemsPedido().isEmpty()){
            throw new BusinessException("Pedido deve possuir itens para calcular frete");
        }

        pedido.getOpcoesFrete().clear();
        FreteGateway gateway = freteGatewayResolver.resolve(FreteProvider.MOCK);
        List<OpcaoFretePedido> opcoesFrete = gateway.calcularFrete(pedido);
        List<OpcaoFreteDTO> response = new ArrayList<>();
        for(OpcaoFretePedido opcaoFrete: opcoesFrete){
            opcaoFrete.setPedido(pedido);
            pedido.getOpcoesFrete().add(opcaoFrete);
            response.add(opcaoFreteMapper.toDTO(opcaoFrete));
        }
        return response;
    }


    //HELPERS
    private EnderecoPedidoDTO resolveEndereco(Pedido pedido, EnderecoPedidoRequest request) {
        if(request.enderecoId() != null){
            return resolveEnderecoExistente(pedido, request);
        }
        return resolveEnderecoManual(request);
    }

    private EnderecoPedidoDTO resolveEnderecoManual(EnderecoPedidoRequest request) {
        if(request.cep() == null ||
        request.estado() == null ||
        request.cidade() == null ||
        request.bairro() == null ||
        request.logradouro() == null ||
        request.numero() == null ){
            throw new BusinessException("Dados do endereço são obrigatórios");
        }
        return new EnderecoPedidoDTO(
                request.cep(),
                request.estado(),
                request.cidade(),
                request.bairro(),
                request.logradouro(),
                request.numero(),
                request.complemento(),
                request.pontoReferencia()
        );
    }

    private EnderecoPedidoDTO resolveEnderecoExistente(Pedido pedido, EnderecoPedidoRequest request) {
        if(request.cep() != null ||
        request.estado() != null ||
        request.cidade() != null ||
        request.bairro() != null ||
        request.logradouro() != null ||
        request.numero() != null ||
        request.complemento() != null ||
        request.pontoReferencia() != null){
            throw new BusinessException("Dados do endereço não devem ser enviados ao informar o enderecoId");
        }
        if(pedido.getCliente() == null || pedido.getCliente().getId() == null){
            throw new BusinessException("Cliente não informado");
        }
        Endereco endereco = enderecoRepository.findByIdAndClienteId(request.enderecoId(), pedido.getCliente().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Endereço não localizado")
        );
        return enderecoMapper.toEnderecoPedidoDTO(endereco);
    }


    public ClienteSnapshot resolveCliente(PedidoRascunhoRequest request){
        if(request.clienteId() != null){
            return resolveClienteCadastrado(request);
        }
        return resolveClienteAvulso(request);
    }

    public ClienteSnapshot resolveClienteCadastrado(PedidoRascunhoRequest request){
        if(request.nomeCliente() != null || request.documentoCliente() != null || request.telefone() != null || request.tipoCliente() != null){
            throw new BusinessException("Dados do cliente não devem ser enviados ao inserir o Id do Cliente");
        }
        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(request.clienteId()).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );
        return new ClienteSnapshot(cliente, cliente.getNome(), cliente.getNumeroDocumento(), cliente.getTelefone(), cliente.getTipoCliente());
    }

    public ClienteSnapshot resolveClienteAvulso(PedidoRascunhoRequest request){
        if(request.nomeCliente() == null || request.documentoCliente() == null || request.telefone() == null || request.tipoCliente() == null){
            throw new BusinessException("Dados do Cliente são obrigatórios para cliente avulso");
        }
        return new ClienteSnapshot(null, request.nomeCliente(), request.documentoCliente(), request.telefone(), request.tipoCliente());
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
          ProdutoVariacao produto = produtosMap.get(item.id());
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

    private void validaPedidoRascunho(Pedido pedido) {
        if(pedido.getStatusPedido() != StatusPedido.RASCUNHO){
            throw new BusinessException("Pedido não pode ser alterado");
        }
    }



    public PedidoMinDTO alterarStatusPedido(PedidoAdminRequest request) {
        return null;
    }


}

