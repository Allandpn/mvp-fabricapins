-- =========================================
-- PEDIDOS
-- =========================================

CREATE INDEX idx_pedido_cliente
    ON tb_pedido(cliente_id);

CREATE INDEX idx_pedido_status
    ON tb_pedido(status_pedido);

CREATE INDEX idx_pedido_data_criacao
    ON tb_pedido(data_criacao);

CREATE INDEX idx_pedido_data_pagamento
    ON tb_pedido(data_pagamento_confirmado);

CREATE INDEX idx_pedido_origem
    ON tb_pedido(origem_pedido);

CREATE INDEX idx_pedido_tipo_cliente
    ON tb_pedido(tipo_cliente);

CREATE INDEX idx_pedido_status_data
    ON tb_pedido(status_pedido, data_criacao);

-- =========================================
-- ITENS PEDIDO
-- =========================================

CREATE INDEX idx_item_pedido_pedido
    ON tb_item_pedido(pedido_id);

CREATE INDEX idx_item_pedido_produto
    ON tb_item_pedido(produto_id);

-- =========================================
-- PRODUTOS
-- =========================================

CREATE INDEX idx_produto_categoria
    ON tb_produto(categoria_id);

CREATE INDEX idx_produto_ativo
    ON tb_produto(ativo);

CREATE INDEX idx_produto_tipo_estoque
    ON tb_produto(tipo_estoque);

CREATE INDEX idx_produto_nome
    ON tb_produto(nome);

-- =========================================
-- CLIENTES
-- =========================================

CREATE INDEX idx_cliente_tipo
    ON tb_cliente(tipo_cliente);

CREATE INDEX idx_cliente_data_cadastro
    ON tb_cliente(data_cadastro);

-- =========================================
-- ENDERECOS
-- =========================================

CREATE INDEX idx_endereco_cliente
    ON tb_endereco(cliente_id);

-- =========================================
-- CUPONS
-- =========================================

CREATE INDEX idx_cupom_ativo
    ON tb_cupom_desconto(ativo);

CREATE INDEX idx_cupom_validade
    ON tb_cupom_desconto(data_validade);
