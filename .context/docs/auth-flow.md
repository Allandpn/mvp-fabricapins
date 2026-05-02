Autenticação:
- JWT stateless
- Endpoint /auth/login gera token
- Token enviado via Authorization Bearer

Autorização:
- Roles: ADMIN, USER
- Controle via @PreAuthorize

Stack:
- Spring Boot 3
- Spring Security