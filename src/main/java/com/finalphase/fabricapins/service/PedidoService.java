package com.finalphase.fabricapins.service;

import com.finalphase.fabricapins.domain.entities.*;
import com.finalphase.fabricapins.domain.enums.FreteProvider;
import com.finalphase.fabricapins.domain.enums.ParametroChave;
import com.finalphase.fabricapins.domain.enums.StatusPedido;
import com.finalphase.fabricapins.domain.enums.TipoCliente;
import com.finalphase.fabricapins.dto.PedidoCupom.CupomRequest;
import com.finalphase.fabricapins.dto.cliente.ClienteSnapshot;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoDTO;
import com.finalphase.fabricapins.dto.endereco.EnderecoPedidoRequest;
import com.finalphase.fabricapins.dto.frete.FreteRequest;
import com.finalphase.fabricapins.dto.frete.OpcaoFreteDTO;
import com.finalphase.fabricapins.dto.item_pedido.ItemPedidoRequest;
import com.finalphase.fabricapins.dto.parametro.ParametroDTO;
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
import jakarta.validation.Valid;
import jdk.jshell.Snippet;
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
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ProdutoVariacaoService produtoVariacaoService;
    @Autowired
    private ItemPedidoService itemPedidoService;
    @Autowired
    private CupomDescontoService cupomDescontoService;
    @Autowired
    private ParametroService parametroService;
    @Autowired
    private EstoqueService estoqueService;

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
    public PedidoDTO insertPedidoCompleto(PedidoAdminRequest request) {
       validaSeListaItensVazia(request);
       ClienteSnapshot cliente = resolveClienteAdmin(request);
       Pedido pedido = new Pedido(cliente);
       pedido.setCodigoPedido(pedido.gerarCodigoPedido());
       pedido.setOrigemPedido(request.origemPedido());
       pedido.setStatusPedido(StatusPedido.AGUARDANDO_PAGAMENTO);
       pedido.setObservacao(request.observacao());
       adicionarItens(pedido, request.items());
       definirEnderecoPedido(pedido, request.enderecoEntrega());
       aplicarCupons(pedido, request.cupons());
       pedido.incluirFreteAdmin(request.valorFrete());
       estoqueService.reservarEstoque(pedido.getItemsPedido());
       pedido = pedidoRepository.save(pedido);
       return mapper.toDTO(pedido);
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
    public PedidoDTO adicionarItemPedido(Long pedidoId, ItemPedidoRequest request) {
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

        }
        else {
            ItemPedido item = itemPedidoService.createItemPedido(
                    request,
                    produto,
                    pedido.getTipoCliente()
            );
            pedido.adicionarItem(item);
        }
        pedidoRepository.save(pedido);
        return mapper.toDTO(pedido);
    }

    @Transactional
    public PedidoDTO alterarItemPedido(Long pedidoId, Long itemId, Integer quantidade) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        // remove item existente
        ItemPedido item = pedido.getItemsPedido()
                .stream()
                .filter(x -> x.getProdutoVariacao().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não possui esse item"));
        pedido.atualizarQuantidade(item, quantidade);
        pedidoRepository.save(pedido);
        return mapper.toDTO(pedido);
    }


    @Transactional
    public PedidoDTO removerItemPedido(Long pedidoId, Long itemId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        // remove item existente
        ItemPedido item = pedido.getItemsPedido()
                .stream()
                .filter(x -> x.getProdutoVariacao().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não possui esse item"));
        pedido.removerItem(item);
        pedidoRepository.save(pedido);
        return mapper.toDTO(pedido);
    }


    // adicionar endereco ao pedido
    @Transactional
    public PedidoDTO definirEndereco(Long pedidoId, EnderecoPedidoRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        EnderecoPedidoDTO endereco = resolveEndereco(pedido, request);
        pedido.definirEndereco(endereco);
        pedidoRepository.save(pedido);
        return mapper.toDTO(pedido);
    }

    // define frete
    @Transactional()
    public PedidoDTO definirFrete(Long pedidoId, FreteRequest request){
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
        if (pedido.getOpcoesFrete().isEmpty()){
            throw new BusinessException("Frete ainda não foi calculado");
        }
        OpcaoFretePedido opcao = pedido.getOpcoesFrete()
                .stream()
                .filter(x -> x.getServiceId().equals(request.serviceId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Opção de frete inválida"));

        pedido.definirFrete(opcao);
        pedidoRepository.save(pedido);
        return mapper.toDTO(pedido);
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
        ParametroDTO provider =  parametroService.getParametro(ParametroChave.FRETE_PROVIDER_PADRAO);
        FreteGateway gateway = freteGatewayResolver.resolve(FreteProvider.valueOf(provider.valor()));
        List<OpcaoFretePedido> opcoesFrete = gateway.calcularFrete(pedido);
        List<OpcaoFreteDTO> response = new ArrayList<>();
        for(OpcaoFretePedido opcaoFrete: opcoesFrete){
            opcaoFrete.setPedido(pedido);
            opcaoFrete.setProvider(provider.toString());
            pedido.getOpcoesFrete().add(opcaoFrete);
            response.add(opcaoFreteMapper.toDTO(opcaoFrete));
        }
        return response;
    }

    @Transactional()
    public PedidoDTO adicionarCupom(Long pedidoId, @Valid CupomRequest request) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        if(pedido.getItemsPedido().isEmpty()){
            throw new BusinessException("Pedido deve possuir itens para adicionar cupom de desconto");
        }
        CupomDesconto cupom = cupomDescontoService.findByCodigo(request.codigo());
        cupomDescontoService.validarLimiteUso(cupom);
        pedido.aplicarCupom(cupom);
        pedidoRepository.flush(); // persiste dataAplicacao do cupom
        return mapper.toDTO(pedido);
    }

    @Transactional()
    public PedidoDTO removerCupom(Long pedidoId, String codigo) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        validaPedidoRascunho(pedido);
        if(pedido.getCupons().isEmpty()){
            throw new BusinessException("Pedido não possui cupons de desconto");
        }
        boolean cupomExiste = pedido.getCupons().stream().anyMatch(x -> x.getCodigoCupom().equals(codigo));
        if(!cupomExiste){
            throw new BusinessException("Cupom não encontrado no pedido");
        }
        pedido.removerCupom(codigo);
        pedidoRepository.flush(); // persiste dataAplicacao do cupom
        return mapper.toDTO(pedido);
    }

    @Transactional()
    public PedidoDTO confirmarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        pedido.confirmar();
        estoqueService.reservarEstoque(pedido.getItemsPedido());
        return mapper.toDTO(pedido);
    }

    @Transactional()
    public PedidoMinDTO cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        StatusPedido statusPedidoOriginal = pedido.getStatusPedido();
        pedido.cancelar();
        if(statusPedidoOriginal != StatusPedido.RASCUNHO){
            estoqueService.devolverEstoque(pedido.getItemsPedido());
        }
        return mapper.toMinDTO(pedido);
    }

    @Transactional()
    public PedidoMinDTO confirmarPagamento(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        pedido.confirmarPagamento();
        return mapper.toMinDTO(pedido);
    }

    @Transactional()
    public PedidoMinDTO enviarProducao(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(
                () -> new ResourceNotFoundException("Pedido não encontrado")
        );
        pedido.iniciarProducao();
        return mapper.toMinDTO(pedido);
    }


    //HELPERS

    private EnderecoPedidoDTO resolveEndereco(Pedido pedido, EnderecoPedidoRequest request) {
        if(request.enderecoId() != null){
            if(pedido.getCliente() == null || pedido.getCliente().getId() == null){
                throw new BusinessException("enderecoId só pode ser informado com clienteId");
            }
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

    private ClienteSnapshot resolveClienteAdmin(PedidoAdminRequest request){
        if(request.clienteId() != null){
            return resolveClienteCadastrado(request.clienteId(), request.nomeCliente(), request.documentoCliente(), request.telefone(), request.tipoCliente());
        }
        return resolveClienteAvulso(request.nomeCliente(), request.documentoCliente(), request.telefone(), request.tipoCliente());
    }


    private ClienteSnapshot resolveCliente(PedidoRascunhoRequest request){
        if(request.clienteId() != null){
            return resolveClienteCadastrado(request.clienteId(), request.nomeCliente(), request.documentoCliente(), request.telefone(), request.tipoCliente());
        }
        return resolveClienteAvulso(request.nomeCliente(), request.documentoCliente(), request.telefone(), request.tipoCliente());
    }

    private ClienteSnapshot resolveClienteCadastrado(Long clienteId, String nomeCliente, String documentoCliente, String telefone, TipoCliente tipoCliente){
        if(nomeCliente != null || documentoCliente != null || telefone != null || tipoCliente != null){
            throw new BusinessException("Dados do cliente não devem ser enviados ao inserir o Id do Cliente");
        }
        Cliente cliente = clienteRepository.findByIdAndAtivoTrue(clienteId).orElseThrow(
                () -> new ResourceNotFoundException("Cliente não encontrado")
        );
        return new ClienteSnapshot(cliente, cliente.getNome(), cliente.getNumeroDocumento(), cliente.getTelefone(), cliente.getTipoCliente());
    }

    private ClienteSnapshot resolveClienteAvulso(String nomeCliente, String documentoCliente, String telefone, TipoCliente tipoCliente){
        if(nomeCliente == null || documentoCliente == null || telefone == null || tipoCliente == null){
            throw new BusinessException("Dados do Cliente são obrigatórios para cliente avulso");
        }
        return new ClienteSnapshot(null, nomeCliente, documentoCliente, telefone, tipoCliente);
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

    private void adicionarItens(Pedido pedido, List<ItemPedidoRequest> items){
        List<ProdutoVariacao> produtos = produtoVariacaoService.buscarProdutos(items);
        List<ItemPedido> itemsPedido = criarItemPedido(produtos, items, pedido.getTipoCliente());
        for(ItemPedido item : itemsPedido){
            pedido.adicionarItem(item);
        }
    }


    private void aplicarFrete(Pedido pedido, PedidoAdminRequest request) {
        if(request.valorFrete() != null){
            pedido.setValorFrete(request.valorFrete());
        }
    }

    private void aplicarCupons(Pedido pedido, Set<String> cupons) {
        if (cupons == null || cupons.isEmpty()){
            return;
        }
        List<CupomDesconto> lista = buscarCupons(cupons);
        for(CupomDesconto cupom : lista){
            cupomDescontoService.validarLimiteUso(cupom);
            pedido.aplicarCupom(cupom);
        }
    }


    private void definirEnderecoPedido(Pedido pedido, EnderecoPedidoRequest request) {
        if(request == null){
            throw new BusinessException("Endereço é obrigatório");
        }
        EnderecoPedidoDTO endereco = resolveEndereco(pedido, request);
        pedido.definirEndereco(endereco);
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



    private PedidoDTO alterarStatusPedido(PedidoAdminRequest request) {
        return null;
    }


}

