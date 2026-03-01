-- =============================================
-- SEED - FabricaPins MVP
-- H2 Database - DEV
-- =============================================

-- =============================================
-- PERFIS (5)
-- =============================================
INSERT INTO tb_perfil (id, nome) VALUES (1, 'ROLE_ADMIN');
INSERT INTO tb_perfil (id, nome) VALUES (2, 'ROLE_GERENTE');
INSERT INTO tb_perfil (id, nome) VALUES (3, 'ROLE_VENDEDOR');
INSERT INTO tb_perfil (id, nome) VALUES (4, 'ROLE_SUPORTE');
INSERT INTO tb_perfil (id, nome) VALUES (5, 'ROLE_CLIENTE');

-- =============================================
-- CLIENTES (20)
-- =============================================
INSERT INTO tb_cliente (id, nome, cpf, email, telefone, tipo_cliente, data_cadastro, ativo)
SELECT
x,
CONCAT('Cliente ',x),
LPAD(x,11,'0'),
CONCAT('cliente',x,'@email.com'),
CONCAT('1199999',LPAD(x,4,'0')),
CASE WHEN MOD(x,2)=0 THEN 'REVENDA' ELSE 'VAREJO' END,
CURRENT_TIMESTAMP,
true
FROM SYSTEM_RANGE(1,20);

-- =============================================
-- USUARIOS (20 + ADMIN)
-- =============================================
INSERT INTO tb_usuario (id, username, password, ativo, data_criacao, cliente_id)
SELECT
x,
CONCAT('user',x),
'$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ',
true,
CURRENT_TIMESTAMP,
x
FROM SYSTEM_RANGE(1,20);

INSERT INTO tb_usuario (id, username, password, ativo, data_criacao, cliente_id)
VALUES (100,'admin',
'$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ',
true,
CURRENT_TIMESTAMP,
NULL);

-- PERFIL_USUARIO
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id)
SELECT id,5 FROM tb_usuario WHERE id <= 20;

INSERT INTO tb_perfil_usuario (usuario_id, perfil_id)
VALUES (100,1);

-- =============================================
-- CATEGORIAS (5)
-- =============================================
INSERT INTO tb_categoria (id,nome,descricao,ativa) VALUES
(1,'Pins','Pins personalizados',true),
(2,'Broches','Broches decorativos',true),
(3,'Chaveiros','Chaveiros personalizados',true),
(4,'Adesivos','Adesivos personalizados',true),
(5,'Kits','Kits promocionais',true);

-- =============================================
-- PRODUTOS (50)
-- =============================================
INSERT INTO tb_produto
(id,nome,descricao,tipo_estoque,preco_varejo,preco_revenda,custo_producao,sku,data_cadastro,destaque,ativo,categoria_id)
SELECT
x,
CONCAT('Produto ',x),
CONCAT('Descricao Produto ',x),
CASE WHEN MOD(x,2)=0 THEN 'ESTOQUE' ELSE 'PRODUCAO' END,
29.90 + x,
19.90 + x,
10.00 + x,
CONCAT('SKU-',x),
CURRENT_TIMESTAMP,
false,
true,
((x-1)/10)+1
FROM SYSTEM_RANGE(1,50);

-- =============================================
-- VARIACOES (100)
-- =============================================
INSERT INTO tb_produto_variacao
(id,nome,quantidade_estoque,estoque_minimo,sku,produto_id)
SELECT
x,
'Padrao',
100,
10,
CONCAT('SKU-',x,'-V1'),
x
FROM SYSTEM_RANGE(1,50);

INSERT INTO tb_produto_variacao
(id,nome,quantidade_estoque,estoque_minimo,sku,produto_id)
SELECT
x+100,
'Premium',
50,
5,
CONCAT('SKU-',x,'-V2'),
x
FROM SYSTEM_RANGE(1,50);

-- =============================================
-- CUPONS (5)
-- =============================================
INSERT INTO tb_cupom_desconto
(id,codigo,ativo,valor_desconto,tipo_desconto,data_validade,limite_usos)
VALUES
(1,'DESC10',true,10,'PERCENTUAL','2026-12-31',100),
(2,'DESC20',true,20,'PERCENTUAL','2026-12-31',100),
(3,'FIXO15',true,15,'FIXO','2026-12-31',50),
(4,'FIXO30',true,30,'FIXO','2026-12-31',50),
(5,'PROMO5',true,5,'PERCENTUAL','2026-12-31',200);

-- =============================================
-- PEDIDOS (50)
-- =============================================
INSERT INTO tb_pedido
(id,data_criacao,status_pedido,origem_pedido,valor_total,valor_subtotal,
numero_pedido,nome_cliente_snapshot,cpf_cnpj_cliente_snapshot,
cep,estado,cidade,bairro,logradouro,numero,cliente_id)
SELECT
x,
CURRENT_TIMESTAMP,
CASE
    WHEN MOD(x,5)=0 THEN 'CANCELADO'
    WHEN MOD(x,4)=0 THEN 'ENTREGUE'
    WHEN MOD(x,3)=0 THEN 'EM_PRODUCAO'
    ELSE 'PAGAMENTO_CONFIRMADO'
END,
CASE
    WHEN MOD(x,3)=0 THEN 'WHATSAPP'
    WHEN MOD(x,2)=0 THEN 'REDE_SOCIAL'
    ELSE 'SITE'
END,
0,
0,
CONCAT('PED-',x),
CONCAT('Cliente ',((x-1)%20)+1),
LPAD(((x-1)%20)+1,11,'0'),
'12345678',
'SP',
'Sao Paulo',
'Centro',
'Rua Teste',
100,
((x-1)%20)+1
FROM SYSTEM_RANGE(1,50);

-- =============================================
-- PAGAMENTOS (50)
-- =============================================
INSERT INTO tb_pagamento
(id,data_pagamento,valor_pago,forma_pagamento,status_pagamento)
SELECT
x,
CURRENT_TIMESTAMP,
99.90,
CASE WHEN MOD(x,2)=0 THEN 'PIX' ELSE 'CARTAO_CREDITO' END,
CASE WHEN MOD(x,5)=0 THEN 'RECUSADO' ELSE 'APROVADO' END
FROM SYSTEM_RANGE(1,50);

-- Vinculando pagamento ao pedido
UPDATE tb_pedido
SET pagamento_id = id
WHERE id <= 50;

-- =============================================
-- ITENS PEDIDO (100)
-- =============================================
INSERT INTO tb_item_pedido
(id,quantidade,preco_unitario,nome_produto_snapshot,custo_unitario_snapshot,pedido_id,produto_variacao_id)
SELECT
x,
2,
39.90,
CONCAT('Produto ',((x-1)%50)+1),
20.00,
((x-1)%50)+1,
((x-1)%50)+1
FROM SYSTEM_RANGE(1,100);