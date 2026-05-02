# Arquitetura do Sistema

- Backend: Spring Boot (API REST)
- Frontend: React
- Arquitetura: Camadas (Controller → Service → Repository)

## Padrões

- Controllers não acessam repository direto
- Services contêm regra de negócio
- DTOs são usados para entrada/saída

## Organização

- cliente/
  - ClienteController
  - ClienteService
  - ClienteRepository