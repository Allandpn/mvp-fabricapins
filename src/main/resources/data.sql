-- =============================================
-- SEED - FabricaPins MVP
-- H2 Database - DEV
-- =============================================

-- =============================================
-- PERFIS (5)
-- =============================================
INSERT INTO tb_perfil (nome) VALUES
('ROLE_ADMIN'),
('ROLE_GERENTE'),
('ROLE_VENDEDOR'),
('ROLE_CLIENTE');


-- =============================================
-- CLIENTES (20)
-- =============================================
INSERT INTO tb_cliente
(nome, email, telefone, tipo_pessoa, numero_documento, tipo_cliente, data_cadastro, ativo)
SELECT
    CONCAT('Cliente ', x),
    CONCAT('cliente', x, '@email.com'),
    CONCAT('1199999', LPAD(x,4,'0')),
    CASE WHEN MOD(x,2)=0 THEN 'FISICA' ELSE 'JURIDICA' END,
    CASE WHEN MOD(x,2)=0 THEN LPAD(x,11,'0') ELSE LPAD(x,14,'0') END,
    CASE WHEN MOD(x,2)=0 THEN 'VAREJO' ELSE 'REVENDA' END,
    CURRENT_TIMESTAMP,
    true
FROM SYSTEM_RANGE(1,20) AS t(x);


-- =============================================
-- USUARIOS (20 + ADMIN)
-- =============================================
INSERT INTO tb_usuario
(username, password, ativo, data_criacao)
SELECT
CONCAT('user', x),
'$2a$10$dkW/YG2RWe3/uCvi6APfxO0XNfbDFQ3NJS3n8IAp/nmdOQVj7sIZG',
true,
CURRENT_TIMESTAMP
FROM SYSTEM_RANGE(1,20) AS t(x);

INSERT INTO tb_usuario
(username, password, ativo, data_criacao)
VALUES (
'admin',
'$2a$10$dkW/YG2RWe3/uCvi6APfxO0XNfbDFQ3NJS3n8IAp/nmdOQVj7sIZG',
true,
CURRENT_TIMESTAMP
);


-- =============================================
-- VINCULAR CLIENTE ↔ USUARIO
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
-- PERFIL_USUARIO
-- =============================================
INSERT INTO tb_perfil_usuario (usuario_id, perfil_id)
SELECT u.id, p.id
FROM tb_usuario u
JOIN tb_perfil p ON p.nome = 'ROLE_CLIENTE'
WHERE u.username LIKE 'user%';

INSERT INTO tb_perfil_usuario (usuario_id, perfil_id)
SELECT u.id, p.id
FROM tb_usuario u
JOIN tb_perfil p ON p.nome = 'ROLE_ADMIN'
WHERE u.username = 'admin';

INSERT INTO tb_perfil_usuario (usuario_id, perfil_id)
SELECT u.id, p.id
FROM tb_usuario u
JOIN tb_perfil p ON p.nome = 'ROLE_VENDEDOR'
WHERE u.id LIKE '%4';

INSERT INTO tb_perfil_usuario (usuario_id, perfil_id)
SELECT u.id, p.id
FROM tb_usuario u
JOIN tb_perfil p ON p.nome = 'ROLE_GERENTE'
WHERE u.id LIKE '%7';


-- =============================================
-- ENDERECOS (30 registros)
-- =============================================

-- Endereço principal (ENTREGA) para todos
INSERT INTO tb_endereco
(cep, estado, cidade, bairro, logradouro, numero,
 endereco_principal, tipo_endereco, apelido,
 data_cadastro, cliente_id)
SELECT
'12345678',
'SP',
'Sao Paulo',
'Centro',
CONCAT('Rua Cliente ', c.id),
'100',
true,
'ENTREGA',
'Casa',
CURRENT_TIMESTAMP,
c.id
FROM tb_cliente c;


-- Endereço de COBRANCA para clientes pares
INSERT INTO tb_endereco
(cep, estado, cidade, bairro, logradouro, numero,
 endereco_principal, tipo_endereco, apelido,
 data_cadastro, cliente_id)
SELECT
'87654321',
'SP',
'Sao Paulo',
'Bela Vista',
CONCAT('Avenida Cliente ', c.id),
'200',
false,
'COBRANCA',
'Escritorio',
CURRENT_TIMESTAMP,
c.id
FROM tb_cliente c
WHERE MOD(c.id,2)=0;


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
(nome, descricao, img_url,
 peso, altura, largura, comprimento,
 slug, data_cadastro, destaque, ativo, categoria_id)
SELECT
CONCAT('Produto ', x),
CONCAT('Descricao Produto ', x),
CONCAT('produto',x,'.png'),

0.2 + (x*0.01),
2.0,
2.0,
2.0,

CONCAT('produto-',x),
CURRENT_TIMESTAMP,
CASE WHEN MOD(x,7)=0 THEN true ELSE false END,
true,
((x-1)/10)+1

FROM SYSTEM_RANGE(1,50) AS t(x);


-- =============================================
-- VARIACOES (100)
-- =============================================

-- Variação padrão (ESTOQUE)
INSERT INTO tb_produto_variacao
(nome, descricao, tipo_estoque,
 quantidade_estoque, estoque_minimo,
 preco_varejo, preco_revenda, custo_producao,
 sku, img_url, ativo, data_cadastro, produto_id)
SELECT
'Padrao',
'Versao padrao',
'ESTOQUE',

100,
10,

29.90 + p.id,
19.90 + p.id,
10.00 + p.id,

CONCAT('SKU-', p.id, '-V1'),
CONCAT('produto',p.id,'-v1.png'),
true,
CURRENT_TIMESTAMP,
p.id

FROM tb_produto p;



-- Variação premium (PRODUCAO)
INSERT INTO tb_produto_variacao
(nome, descricao, tipo_estoque,
 quantidade_estoque, estoque_minimo,
 preco_varejo, preco_revenda, custo_producao,
 data_prevista_envio,
 sku, img_url, ativo, data_cadastro, produto_id)
SELECT
'Premium',
'Versao premium',
'PRODUCAO',

0,
0,

39.90 + p.id,
29.90 + p.id,
15.00 + p.id,

CURRENT_DATE + 7,

CONCAT('SKU-', p.id, '-V2'),
CONCAT('produto',p.id,'-v2.png'),
true,
CURRENT_TIMESTAMP,
p.id

FROM tb_produto p;


-- =============================================
-- CUPONS (5)
-- =============================================
INSERT INTO tb_cupom_desconto
(codigo, ativo, valor_desconto, data_cadastro, tipo_desconto, data_validade, limite_usos)
VALUES
('DESC10',true,10,CURRENT_TIMESTAMP, 'PERCENTUAL','2026-12-31',100),
('DESC20',true,20,CURRENT_TIMESTAMP,'PERCENTUAL','2026-12-31',100),
('FIXO15',true,15,CURRENT_TIMESTAMP,'FIXO','2026-12-31',50),
('FIXO30',true,30,CURRENT_TIMESTAMP,'FIXO','2026-12-31',50),
('PROMO5',true,5,CURRENT_TIMESTAMP,'PERCENTUAL','2026-12-31',200);


-- =============================================
-- PEDIDOS (50)
-- =============================================
-- =============================================
-- PEDIDOS COMPLETOS (ANALYTICS READY)
-- =============================================

INSERT INTO tb_pedido (
    data_criacao,
    data_atualizacao,
    data_conclusao_pedido,

    status_pedido,
    origem_pedido,

    valor_subtotal,
    desconto,
    valor_frete,
    valor_total,

    valor_total_final,
    valor_frete_final,
    valor_desconto_final,

    codigo_pedido,

    frete_service_id,
    data_calculo_frete,
    nome_servico_frete,
    prazo_entrega_dias,
    frete_empresa,

    data_pagamento_confirmado,
    data_inicio_producao,
    data_fim_producao,
    data_separacao,
    data_aguardando_envio,
    data_envio,
    data_entrega,
    data_cancelamento,

    observacao,

    nome_cliente,
    documento_cliente,
    telefone,
    tipo_cliente,

    cep, estado, cidade, bairro, logradouro, numero,

    cliente_id
)
SELECT
    base.data_criacao,
    base.data_criacao,
    base.data_conclusao,

    base.status,
    base.origem,

    base.subtotal,
    base.desconto,
    base.frete,
    base.total,

    base.total,
    base.frete,
    base.desconto,

    CONCAT('PED-', base.id),

    'CORREIOS',
    base.data_criacao,
    'PAC',
    base.prazo,
    'Correios',

    base.data_pagamento,
    base.data_inicio_producao,
    base.data_fim_producao,
    base.data_separacao,
    base.data_aguardando_envio,
    base.data_envio,
    base.data_entrega,
    base.data_cancelamento,

    'Pedido gerado automaticamente',

    c.nome,
    c.numero_documento,
    c.telefone,
    c.tipo_cliente,

    '88100000','SC','São José','Centro','Rua Teste','100',

    c.id

FROM (
         SELECT
             x AS id,

             DATEADD('DAY', -x*2, CURRENT_TIMESTAMP) AS data_criacao,

             -- STATUS
             CASE
                 WHEN MOD(x,10)=0 THEN 'CANCELADO'
                 WHEN MOD(x,6)=0 THEN 'ENTREGUE'
                 WHEN MOD(x,5)=0 THEN 'ENVIADO'
                 WHEN MOD(x,4)=0 THEN 'AGUARDANDO_ENVIO'
                 WHEN MOD(x,3)=0 THEN 'EM_SEPARACAO'
                 WHEN MOD(x,2)=0 THEN 'EM_PRODUCAO'
                 ELSE 'PAGAMENTO_CONFIRMADO'
                 END AS status,

             CASE
                 WHEN MOD(x,3)=0 THEN 'WHATSAPP'
                 WHEN MOD(x,2)=0 THEN 'REDE_SOCIAL'
                 ELSE 'SITE'
                 END AS origem,

             -- FINANCEIRO
             (50 + MOD(x,200)) AS subtotal,
             (MOD(x,3) * 5) AS desconto,
             (10 + MOD(x,15)) AS frete,

             (50 + MOD(x,200)) - (MOD(x,3) * 5) + (10 + MOD(x,15)) AS total,

             -- PRAZO
             3 + MOD(x,5) AS prazo,

             -- TIMELINE
             DATEADD('HOUR', 2, DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_pagamento,

             DATEADD('HOUR', 10, DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_inicio_producao,

             DATEADD('DAY', 1, DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_fim_producao,

             DATEADD('DAY', 2, DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_separacao,

             DATEADD('DAY', 3, DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_aguardando_envio,

             DATEADD('DAY', 4, DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_envio,

             DATEADD('DAY', 4 + (3 + MOD(x,5)), DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_entrega,

             DATEADD('HOUR', 5, DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_cancelamento,

             DATEADD('HOUR', 3, DATEADD('DAY', -x*2, CURRENT_TIMESTAMP)) AS data_conclusao

         FROM SYSTEM_RANGE(1,200) AS t(x)
     ) base
         JOIN tb_cliente c ON c.id = ((base.id-1) % 50) + 1;

UPDATE tb_pedido p
SET
    valor_subtotal = (
        SELECT COALESCE(SUM(i.preco_unitario * i.quantidade),0)
        FROM tb_item_pedido i
        WHERE i.pedido_id = p.id
    ),
    valor_total = (
                      SELECT COALESCE(SUM(i.preco_unitario * i.quantidade),0)
                      FROM tb_item_pedido i
                      WHERE i.pedido_id = p.id
                  ) + COALESCE(p.valor_frete,0);



-- CANCELADOS não têm pipeline completo
UPDATE tb_pedido
SET
    data_pagamento_confirmado = NULL,
    data_inicio_producao = NULL,
    data_fim_producao = NULL,
    data_separacao = NULL,
    data_aguardando_envio = NULL,
    data_envio = NULL,
    data_entrega = NULL
WHERE status_pedido = 'CANCELADO';

-- PAGAMENTO_CONFIRMADO para frente
UPDATE tb_pedido
SET data_inicio_producao = NULL,
    data_fim_producao = NULL,
    data_separacao = NULL,
    data_aguardando_envio = NULL,
    data_envio = NULL,
    data_entrega = NULL
WHERE status_pedido = 'PAGAMENTO_CONFIRMADO';

-- EM_PRODUCAO
UPDATE tb_pedido
SET data_fim_producao = NULL,
    data_separacao = NULL,
    data_aguardando_envio = NULL,
    data_envio = NULL,
    data_entrega = NULL
WHERE status_pedido = 'EM_PRODUCAO';


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
FROM SYSTEM_RANGE(1,50) AS t(x);

-- só pedidos pagos recebem pagamento aprovado
UPDATE tb_pagamento
SET status_pagamento = 'APROVADO'
WHERE id IN (
    SELECT pagamento_id FROM tb_pedido
    WHERE status_pedido = 'PAGAMENTO_CONFIRMADO'
);



-- Vincular pagamento ao pedido
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
 img_produto_snapshot,
 custo_unitario_snapshot, pedido_id, produto_variacao_id)
SELECT
    2,
    39.90,
    CONCAT('Produto ', ((x-1)%50)+1),
    CONCAT('produto', ((x-1)%50)+1, '.png'),
    20.00,
    ((x-1)%50)+1,
    ((x-1)%50)+1
FROM SYSTEM_RANGE(1,100) AS t(x);


-- =============================================
-- PEDIDO_CUPOM
-- =============================================
INSERT INTO tb_pedido_cupom
(pedido_id, cupom_id, data_aplicacao, valor_desconto_aplicado, codigo_cupom, tipo_desconto)
SELECT
p.id,
c.id,
CURRENT_TIMESTAMP,
10,
c.codigo,
c.tipo_desconto
FROM tb_pedido p
JOIN tb_cupom_desconto c ON c.codigo = 'DESC10'
WHERE MOD(p.id,4)=0;

INSERT INTO tb_pedido_cupom
(pedido_id, cupom_id, data_aplicacao, valor_desconto_aplicado, codigo_cupom, tipo_desconto)
SELECT
p.id,
c.id,
CURRENT_TIMESTAMP,
15,
c.codigo,
c.tipo_desconto
FROM tb_pedido p
JOIN tb_cupom_desconto c ON c.codigo = 'FIXO15'
WHERE MOD(p.id,6)=0;


INSERT INTO tb_parametro (chave, valor) VALUES
('CEP_ORIGEM','88600000'),
('FRETE_PROVIDER_PADRAO','MELHOR_ENVIO');