-- =============================================
-- SEED - FabricaPins MVP
-- H2 Database - DEV (IDENTITY COMPATIBLE)
-- =============================================

-- =============================================
-- PERFIS (5)
-- =============================================
INSERT INTO tb_perfil (nome) VALUES ('ROLE_ADMIN');
INSERT INTO tb_perfil (nome) VALUES ('ROLE_GERENTE');
INSERT INTO tb_perfil (nome) VALUES ('ROLE_VENDEDOR');
INSERT INTO tb_perfil (nome) VALUES ('ROLE_SUPORTE');
INSERT INTO tb_perfil (nome) VALUES ('ROLE_CLIENTE');

-- =============================================
-- CLIENTES (20)
-- =============================================
INSERT INTO tb_cliente
(nome, cpf, email, telefone, tipo_cliente, data_cadastro, ativo)
SELECT
CONCAT('Cliente ', x),
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

-- Usuários vinculados aos clientes
INSERT INTO tb_usuario
(username, password, ativo, data_criacao)
SELECT
CONCAT('user', x),
'$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ',
true,
CURRENT_TIMESTAMP
FROM SYSTEM_RANGE(1,20);

-- Admin sem cliente
INSERT INTO tb_usuario
(username, password, ativo, data_criacao)
VALUES (
'admin',
'$2a$10$7QJ8z3e1k2mN4pL6oR0iXuVwYsAtBcDeFgHiJkLmNoPqRsTuVwXyZ',
true,
CURRENT_TIMESTAMP
);

-- =============================================
-- VINCULAR CLIENTES AOS USUARIOS
-- =============================================
UPDATE tb_cliente c
SET usuario_id = (
    SELECT u.id
    FROM tb_usuario u
    WHERE u.username = CONCAT('user',
          REPLACE(c.nome, 'Cliente ', '')
    )
)
WHERE c.nome LIKE 'Cliente %';

-- =============================================
-- PERFIL_USUARIO (ManyToMany)
-- =============================================

-- Todos usuários normais = ROLE_CLIENTE
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id)
SELECT u.id, p.id
FROM tb_usuario u
JOIN tb_perfil p ON p.nome = 'ROLE_CLIENTE'
WHERE u.username LIKE 'user%';

-- Admin = ROLE_ADMIN
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id)
SELECT u.id, p.id
FROM tb_usuario u
JOIN tb_perfil p ON p.nome = 'ROLE_ADMIN'
WHERE u.username = 'admin';

-- =============================================
-- CATEGORIAS (5)
-- =============================================
INSERT INTO tb_categoria (nome, descricao, ativa) VALUES
('Pins','Pins personalizados',true),
('Broches','Broches decorativos',true),
('Chaveiros','Chaveiros personalizados',true),
('Adesivos','Adesivos personalizados',true),
('Kits','Kits promocionais',true);

-- =============================================
-- PRODUTOS (50)
-- =============================================
INSERT INTO tb_produto
(nome, descricao, tipo_estoque, preco_varejo, preco_revenda, custo_producao,
 sku, data_cadastro, destaque, ativo, categoria_id)
SELECT
CONCAT('Produto ', x),
CONCAT('Descricao Produto ', x),
CASE WHEN MOD(x,2)=0 THEN 'ESTOQUE' ELSE 'PRODUCAO' END,
29.90 + x,
19.90 + x,
10.00 + x,
CONCAT('SKU-', x),
CURRENT_TIMESTAMP,
false,
true,
((x-1)/10)+1
FROM SYSTEM_RANGE(1,50);

-- =============================================
-- VARIACOES (100)
-- =============================================
-- Variação padrão
INSERT INTO tb_produto_variacao
(nome, quantidade_estoque, estoque_minimo, sku, produto_id)
SELECT
'Padrao',
100,
10,
CONCAT('SKU-', p.id, '-V1'),
p.id
FROM tb_produto p;

-- Variação premium
INSERT INTO tb_produto_variacao
(nome, quantidade_estoque, estoque_minimo, sku, produto_id)
SELECT
'Premium',
50,
5,
CONCAT('SKU-', p.id, '-V2'),
p.id
FROM tb_produto p;

-- =============================================
-- CUPONS (5)
-- =============================================
INSERT INTO tb_cupom_desconto
(codigo, ativo, valor_desconto, tipo_desconto, data_validade, limite_usos)
VALUES
('DESC10',true,10,'PERCENTUAL','2026-12-31',100),
('DESC20',true,20,'PERCENTUAL','2026-12-31',100),
('FIXO15',true,15,'FIXO','2026-12-31',50),
('FIXO30',true,30,'FIXO','2026-12-31',50),
('PROMO5',true,5,'PERCENTUAL','2026-12-31',200);

-- =============================================
-- PEDIDOS (50)
-- =============================================
INSERT INTO tb_pedido
(data_criacao, status_pedido, origem_pedido, valor_total, valor_subtotal,
 numero_pedido, nome_cliente_snapshot, cpf_cnpj_cliente_snapshot,
 cep, estado, cidade, bairro, logradouro, numero, cliente_id)
SELECT
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
CONCAT('PED-', x),
c.nome,
c.cpf,
'12345678',
'SP',
'Sao Paulo',
'Centro',
'Rua Teste',
100,
c.id
FROM SYSTEM_RANGE(1,50) r
JOIN tb_cliente c ON c.id = ((r.x-1) % 20) + 1;

-- =============================================
-- PAGAMENTOS (50)
-- =============================================
INSERT INTO tb_pagamento
(data_pagamento, valor_pago, forma_pagamento, status_pagamento)
SELECT
CURRENT_TIMESTAMP,
99.90,
CASE WHEN MOD(x,2)=0 THEN 'PIX' ELSE 'CARTAO_CREDITO' END,
CASE WHEN MOD(x,5)=0 THEN 'RECUSADO' ELSE 'APROVADO' END
FROM SYSTEM_RANGE(1,50);

-- Vinculando pagamento ao pedido (SAFE para OneToOne + H2)
MERGE INTO tb_pedido p
USING (
    SELECT
        p2.id AS pedido_id,
        pg.id AS pagamento_id
    FROM
        (SELECT id, ROW_NUMBER() OVER (ORDER BY id) rn FROM tb_pedido) p2
        JOIN
        (SELECT id, ROW_NUMBER() OVER (ORDER BY id) rn FROM tb_pagamento) pg
        ON p2.rn = pg.rn
) x
ON p.id = x.pedido_id
WHEN MATCHED THEN
UPDATE SET p.pagamento_id = x.pagamento_id;

-- =============================================
-- ITENS PEDIDO (100)
-- =============================================
INSERT INTO tb_item_pedido
(quantidade, preco_unitario, nome_produto_snapshot,
 custo_unitario_snapshot, pedido_id, produto_variacao_id)
SELECT
2,
39.90,
CONCAT('Produto ', ((x-1)%50)+1),
20.00,
((x-1)%50)+1,
((x-1)%50)+1
FROM SYSTEM_RANGE(1,100);