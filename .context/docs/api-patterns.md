# API Patterns

## Criar recurso

POST /clientes

- Recebe DTO
- Valida dados
- Chama service
- Retorna 201 CREATED

## Buscar por ID

GET /clientes/{id}

- Retorna 200 ou 404

## Atualizar

PUT /clientes/{id}

- Valida existência