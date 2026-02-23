-- =============================================
-- SEED - FabricaPins MVP
-- H2 Database - DEV
-- =============================================

-- =============================================
-- PERFIL
-- =============================================
INSERT INTO tb_perfil (nome) VALUES ('ROLE_ADMIN');
INSERT INTO tb_perfil (nome) VALUES ('ROLE_OPERADOR');
INSERT INTO tb_perfil (nome) VALUES ('ROLE_CLIENTE');

-- =============================================
-- USUARIO
-- =============================================
INSERT INTO tb_usuario (username, password, ativo, data_criacao, cliente_id)
VALUES ('admin', '$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ', true, NOW(), NULL);

INSERT INTO tb_usuario (username, password, ativo, data_criacao, cliente_id)
VALUES ('operador01', '$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ', true, NOW(), NULL);

INSERT INTO tb_usuario (username, password, ativo, data_criacao, cliente_id)
VALUES ('joao.silva', '$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ', true, NOW(), NULL);

INSERT INTO tb_usuario (username, password, ativo, data_criacao, cliente_id)
VALUES ('maria.souza', '$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ', true, NOW(), NULL);

INSERT INTO tb_usuario (username, password, ativo, data_criacao, cliente_id)
VALUES ('loja.geek', '$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ', true, NOW(), NULL);

-- =============================================
-- PERFIL_USUARIO
-- =============================================
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id) VALUES (1, 1);
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id) VALUES (2, 2);
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id) VALUES (3, 3);
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id) VALUES (4, 3);
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id) VALUES (5, 3);

-- =============================================
-- CLIENTE
-- =============================================
INSERT INTO tb_cliente (nome, cpf, cnpj, email, telefone, tipo_cliente, ativo, data_cadastro)
VALUES ('Joao Silva', '52998224725', NULL, 'joao.silva@email.com', '11987654321', 'VAREJO', true, NOW());

INSERT INTO tb_cliente (nome, cpf, cnpj, email, telefone, tipo_cliente, ativo, data_cadastro)
VALUES ('Maria Souza', '47355170195', NULL, 'maria.souza@email.com', '21912345678', 'VAREJO', true, NOW());

INSERT INTO tb_cliente (nome, cpf, cnpj, email, telefone, tipo_cliente, ativo, data_cadastro)
VALUES ('Carlos Mendes', '71428793860', NULL, 'carlos.mendes@email.com', '31998887766', 'VAREJO', true, NOW());

INSERT INTO tb_cliente (nome, cpf, cnpj, email, telefone, tipo_cliente, ativo, data_cadastro)
VALUES ('Ana Ferreira', '87748248800', NULL, 'ana.ferreira@email.com', '41911223344', 'VAREJO', true, NOW());

INSERT INTO tb_cliente (nome, cpf, cnpj, email, telefone, tipo_cliente, ativo, data_cadastro)
VALUES ('Loja Geek Ltda', '34641016059', '12345678000195', 'contato@lojageek.com.br', '11933445566', 'REVENDA', true, NOW());

INSERT INTO tb_cliente (nome, cpf, cnpj, email, telefone, tipo_cliente, ativo, data_cadastro)
VALUES ('Pins & Cia Comercio', '57646304339', '98765432000100', 'vendas@pinsecia.com.br', '21944556677', 'REVENDA', true, NOW());

INSERT INTO tb_cliente (nome, cpf, cnpj, email, telefone, tipo_cliente, ativo, data_cadastro)
VALUES ('Lucas Oliveira', '61815081328', NULL, 'lucas.oliveira@email.com', '51999887766', 'VAREJO', false, NOW());

-- =============================================
-- ENDERECO
-- =============================================
INSERT INTO tb_endereco (cep, estado, cidade, bairro, logradouro, numero, complemento, ponto_referencia, observacoes, endereco_principal, tipo_endereco, data_cadastro, apelido, cliente_id)
VALUES ('01310100', 'SP', 'Sao Paulo', 'Bela Vista', 'Avenida Paulista', '1000', 'Apto 42', NULL, NULL, true, 'ENTREGA', NOW(), 'Casa', 1);

INSERT INTO tb_endereco (cep, estado, cidade, bairro, logradouro, numero, complemento, ponto_referencia, observacoes, endereco_principal, tipo_endereco, data_cadastro, apelido, cliente_id)
VALUES ('01310100', 'SP', 'Sao Paulo', 'Bela Vista', 'Avenida Paulista', '1000', 'Apto 42', NULL, NULL, false, 'COBRANCA', NOW(), 'Cobranca Casa', 1);

INSERT INTO tb_endereco (cep, estado, cidade, bairro, logradouro, numero, complemento, ponto_referencia, observacoes, endereco_principal, tipo_endereco, data_cadastro, apelido, cliente_id)
VALUES ('20040020', 'RJ', 'Rio de Janeiro', 'Centro', 'Rua da Assembleia', '55', NULL, 'Proximo ao metro', NULL, true, 'ENTREGA', NOW(), 'Trabalho', 2);

-- =============================================
-- CATEGORIA
-- =============================================
INSERT INTO tb_categoria (nome, descricao, ativa)
VALUES ('Pins Anime', 'Pins e broches de personagens de anime e manga', true);

INSERT INTO tb_categoria (nome, descricao, ativa)
VALUES ('Pins Games', 'Pins de personagens e logos de jogos', true);

INSERT INTO tb_categoria (nome, descricao, ativa)
VALUES ('Pins Series e Filmes', 'Pins de series de TV e filmes', true);

INSERT INTO tb_categoria (nome, descricao, ativa)
VALUES ('Pins Geek Geral', 'Pins de cultura geek em geral', true);

INSERT INTO tb_categoria (nome, descricao, ativa)
VALUES ('Patches Bordados', 'Patches e patches bordados para roupas', true);

INSERT INTO tb_categoria (nome, descricao, ativa)
VALUES ('Acessorios', 'Chaveiros e outros acessorios customizados', true);

-- =============================================
-- PRODUTO
-- =============================================
INSERT INTO tb_produto (nome, descricao, tipo_estoque, preco_varejo, preco_revenda, img_url, sku, destaque, ativo, data_cadastro, categoria_id)
VALUES ('Pin Naruto Uzumaki', 'Pin metalico esmaltado do Naruto modo sennin. Acabamento premium, 4cm.', 'ESTOQUE', 29.90, 18.00, NULL, 'PIN-ANI-001', true, true, NOW(), 1);

INSERT INTO tb_produto (nome, descricao, tipo_estoque, preco_varejo, preco_revenda, img_url, sku, destaque, ativo, data_cadastro, categoria_id)
VALUES ('Pin Demon Slayer - Tanjiro', 'Pin do Tanjiro Kamado com hanafuda. Esmaltado dupla face, 3.5cm.', 'ESTOQUE', 27.90, 16.50, NULL, 'PIN-ANI-002', true, true, NOW(), 1);

INSERT INTO tb_produto (nome, descricao, tipo_estoque, preco_varejo, preco_revenda, img_url, sku, destaque, ativo, data_cadastro, categoria_id)
VALUES ('Pin Zelda Triforce', 'Pin da Triforce.', 'ESTOQUE', 32.90, 20.00, NULL, 'PIN-GAM-001', true, true, NOW(), 2);

-- =============================================
-- PRODUTO_VARIACAO
-- =============================================
INSERT INTO tb_produto_variacao (nome, descricao, quantidade_estoque, estoque_minimo, sku, img_url, produto_id)
VALUES ('Dourado', 'Acabamento dourado brilhante', 50, 10, 'PIN-ANI-001-DOU', NULL, 1);

INSERT INTO tb_produto_variacao (nome, descricao, quantidade_estoque, estoque_minimo, sku, img_url, produto_id)
VALUES ('Padrao', 'Versao padrao', 60, 10, 'PIN-ANI-002-PAD', NULL, 2);

-- =============================================
-- CUPOM DESCONTO
-- =============================================
INSERT INTO tb_cupom_desconto (codigo, ativo, valor_desconto, tipo_desconto, data_validade, quantidade_minima_itens, valor_minimo_pedido, limite_usos)
VALUES ('BEMVINDO10', true, 10.00, 'PERCENTUAL', DATEADD('MONTH', 6, CURRENT_DATE), NULL, 50.00, 100);

-- =============================================
-- PAGAMENTO
-- =============================================
INSERT INTO tb_pagamento (data_pagamento, valor_pago, forma_pagamento, status_pagamento, codigo_transacao, gateway_pagamento, parcelas_cartao, data_confirmacao, motivo_recusa)
VALUES (NOW(), 82.70, 'PIX', 'APROVADO', 'PIX-2024-001-ABC', 'MercadoPago', NULL, NOW(), NULL);

-- =============================================
-- PEDIDO
-- =============================================
INSERT INTO tb_pedido (data_criacao, status_pedido, valor_total, valor_subtotal, desconto, numero_pedido, valor_frete,
    nome_cliente_snapshot, cpf_cnpj_cliente_snapshot,
    cep, estado, cidade, bairro, logradouro, numero, complemento,
    cliente_id, pagamento_id)
VALUES (NOW(), 'ENTREGUE', 82.70, 79.70, 0.00, 'PED-2024-001', 3.00,
    'Joao Silva', '52998224725',
    '01310100', 'SP', 'Sao Paulo', 'Bela Vista', 'Avenida Paulista', 1000, 'Apto 42',
    1, 1);

-- =============================================
-- ITEM_PEDIDO
-- =============================================
INSERT INTO tb_item_pedido (quantidade, preco_unitario, nome_produto_snapshot, img_produto_snapshot, pedido_id, produto_variacao_id)
VALUES (1, 29.90, 'Pin Naruto Uzumaki - Dourado', NULL, 1, 1);

-- =============================================
-- PEDIDO_CUPOM
-- =============================================
INSERT INTO tb_pedido_cupom (pedido_id, cupom_desconto_id, data_aplicacao, valor_desconto_aplicado)
VALUES (1, 1, CURRENT_DATE, 5.00);