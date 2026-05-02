# Regras de Backend

## Estrutura
- Sempre usar DTO para entrada e saída
- Nunca expor entidades diretamente

## Controllers
- Devem ser finos (sem regra de negócio)
- Apenas delegam para services

## Services
- Contêm regras de negócio
- Validam regras além das annotations

## Validação
- Usar Bean Validation (@NotNull, @Size)

## Respostas
- Usar ResponseEntity
- Padronizar retorno:
  - success
  - message
  - data