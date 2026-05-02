# 🔐 Auditoria de Segurança — FabricaPins MVP
> **Data:** Maio/2026  
> **Base:** OWASP Top 10 (2021) + Spring Security Best Practices  
> **Stack:** Spring Boot 3.3.5 · Spring Security · JWT (jjwt 0.12.5) · Java 21

---

## Sumário Executivo

| Severidade | Quantidade |
|------------|-----------|
| 🔴 Alto    | 5         |
| 🟡 Médio   | 6         |
| 🟢 Baixo   | 3         |
| ✅ Correto  | 8         |

---

## 🔴 Vulnerabilidades de Risco ALTO

---

### [ALTO-01] CORS Totalmente Aberto — `CorsConfig.java`
**OWASP:** A05:2021 – Security Misconfiguration

**Problema:**
```java
// CorsConfig.java — INSEGURO
registry.addMapping("/**")
        .allowedOrigins("*")       // ← qualquer origem
        .allowedMethods("*")       // ← qualquer método HTTP
        .allowedHeaders("*");      // ← qualquer header
```

`allowedOrigins("*")` permite que **qualquer site na internet** faça requisições autenticadas à sua API. Em APIs REST com JWT, isso facilita ataques de **Cross-Origin Request Forgery** e **data exfiltration** via scripts maliciosos em outros domínios.

**Agravante:** A configuração de CORS no `WebMvcConfigurer` **não é aplicada pelo Spring Security** para rotas protegidas — o filtro de CORS do Security precisa ser configurado separadamente via `http.cors(...)`. Isso significa que a configuração atual pode ser ignorada silenciosamente para requisições preflight em rotas autenticadas.

**Correção:**
```java
// CorsConfig.java — SEGURO
@Configuration
public class CorsConfig {

    // Defina as origens permitidas via propriedade de ambiente
    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);                    // origens explícitas
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);                            // necessário para cookies/auth
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

```java
// SecurityConfig.java — adicionar .cors() referenciando o bean acima
@Autowired
private CorsConfigurationSource corsConfigurationSource;

http
    .cors(cors -> cors.configurationSource(corsConfigurationSource))
    .csrf(csrf -> csrf.disable())
    // ...
```

```properties
# application-prod.properties
app.cors.allowed-origins=https://fabricapins.com.br,https://admin.fabricapins.com.br

# application-dev.properties
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

---

### [ALTO-02] H2 Console Exposto Sem Proteção — `SecurityConfig.java`
**OWASP:** A05:2021 – Security Misconfiguration

**Problema:**
```java
// SecurityConfig.java — INSEGURO
.requestMatchers("/h2-console/**").permitAll()   // ← acesso público ao banco!

// + frameOptions desabilitado globalmente
.headers(headers -> headers
        .frameOptions(frame -> frame.disable())  // ← permite clickjacking em TODA a app
)
```

O console H2 é uma **interface web completa de banco de dados**. Exposto publicamente, permite que qualquer pessoa execute SQL arbitrário, incluindo `DROP TABLE`, `SELECT * FROM tb_usuario` (senhas hash), etc. O `frameOptions().disable()` foi adicionado apenas para o H2 funcionar, mas desabilita proteção contra **Clickjacking** em toda a aplicação.

**Correção:**
```java
// SecurityConfig.java — SEGURO
http
    .authorizeHttpRequests(auth -> auth
        // H2 Console: apenas em dev, apenas para ADMIN
        .requestMatchers("/h2-console/**").hasRole("ADMIN")
        // ...
    )
    .headers(headers -> headers
        // Reabilitar frameOptions com SAMEORIGIN (protege contra clickjacking)
        // e liberar apenas para o H2 console via política específica
        .frameOptions(frame -> frame.sameOrigin())
        .httpStrictTransportSecurity(hsts -> hsts
            .includeSubDomains(true)
            .maxAgeInSeconds(31536000)
        )
    );
```

**Melhor prática:** Desabilitar o H2 Console completamente em produção (já feito em `application-prod.properties`), mas garantir que o perfil ativo correto seja sempre usado:

```java
// Usar @Profile para garantir que a permissão do H2 só exista em dev
@Bean
@Profile("dev")
public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
    // configuração com /h2-console liberado
}

@Bean
@Profile("!dev")
public SecurityFilterChain prodFilterChain(HttpSecurity http) throws Exception {
    // configuração sem /h2-console
}
```

---

### [ALTO-03] Endpoint `/actuator/**` Totalmente Exposto — `SecurityConfig.java`
**OWASP:** A05:2021 – Security Misconfiguration

**Problema:**
```java
// SecurityConfig.java — INSEGURO
.requestMatchers("/actuator/**").permitAll()   // ← expõe métricas, env, beans, etc.
```

O Spring Actuator expõe endpoints como `/actuator/env` (variáveis de ambiente, incluindo secrets), `/actuator/beans` (todos os beans Spring), `/actuator/mappings` (todos os endpoints da API), `/actuator/heapdump` (dump da memória JVM). Mesmo que em produção apenas `/health` esteja configurado, a regra `permitAll()` em `/**` é perigosa se a configuração do Actuator mudar.

**Correção:**
```java
// SecurityConfig.java — SEGURO
.authorizeHttpRequests(auth -> auth
    // Apenas /health público (para health check do Render)
    .requestMatchers("/actuator/health").permitAll()
    // Demais endpoints do actuator: apenas ADMIN
    .requestMatchers("/actuator/**").hasRole("ADMIN")
    // ...
)
```

```properties
# application-prod.properties — já está correto, mas reforçar:
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=when-authorized
```

---

### [ALTO-04] Ausência de Rate Limiting / Proteção contra Brute Force — `AuthController.java`
**OWASP:** A07:2021 – Identification and Authentication Failures

**Problema:**
```java
// AuthController.java — sem nenhuma proteção contra tentativas repetidas
@PostMapping("/login")
public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password())
    );
    // ...
}
```

Não há **nenhum mecanismo** de:
- Rate limiting por IP ou por username
- Bloqueio de conta após N tentativas falhas
- CAPTCHA após tentativas suspeitas
- Logging de tentativas de login falhas

Um atacante pode tentar milhares de senhas por segundo sem qualquer impedimento.

**Correção — Opção 1: Bucket4j (Rate Limiting por IP):**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
```

```java
// RateLimitFilter.java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
            .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (request.getRequestURI().equals("/auth/login")) {
            String ip = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());

            if (!bucket.tryConsume(1)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("{\"error\": \"Muitas tentativas. Tente novamente em 1 minuto.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
```

**Correção — Opção 2: Bloqueio de conta após falhas (já há suporte parcial):**
```java
// UsuarioService.java — adicionar campo de tentativas
// Na entidade Usuario:
@Column(nullable = false)
private int tentativasFalhas = 0;

@Column
private Instant bloqueadoAte;

// No AuthController, capturar BadCredentialsException:
@PostMapping("/login")
public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO request) {
    try {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        usuarioService.resetarTentativas(request.username()); // reset em sucesso
    } catch (BadCredentialsException e) {
        usuarioService.registrarTentativaFalha(request.username());
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }
    // ...
}
```

---

### [ALTO-05] Endpoint `/usuarios/**` Totalmente Público — `SecurityConfig.java`
**OWASP:** A01:2021 – Broken Access Control

**Problema:**
```java
// SecurityConfig.java — INSEGURO
.requestMatchers("/usuarios/**").permitAll()   // ← qualquer pessoa pode criar usuários!
```

O endpoint `POST /usuarios` (criação de usuário) precisa ser público para auto-cadastro, mas `GET /usuarios`, `PUT /usuarios/{id}` e `DELETE /usuarios/{id}` estão protegidos via `@PreAuthorize` no controller. O problema é que o `permitAll()` no `SecurityConfig` **bypassa o filtro de autenticação**, fazendo com que o `SecurityContextHolder` fique vazio — e as anotações `@PreAuthorize` dependem do contexto de segurança populado pelo `JwtFilter`.

**Risco real:** Se houver qualquer falha na lógica do `@PreAuthorize` ou se um endpoint não tiver a anotação, ele ficará completamente aberto.

**Correção:**
```java
// SecurityConfig.java — SEGURO
.authorizeHttpRequests(auth -> auth
    // Apenas o POST de criação é público
    .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
    // Demais operações exigem autenticação (o @PreAuthorize refina ainda mais)
    .requestMatchers("/usuarios/**").authenticated()
    // ...
)
```

---

## 🟡 Vulnerabilidades de Risco MÉDIO

---

### [MÉDIO-01] Ausência de Headers de Segurança HTTP — `SecurityConfig.java`
**OWASP:** A05:2021 – Security Misconfiguration

**Problema:** O Spring Security por padrão adiciona alguns headers, mas `frameOptions().disable()` remove a proteção contra Clickjacking, e não há configuração explícita de:
- `Content-Security-Policy` (CSP) — proteção contra XSS
- `X-Content-Type-Options` — proteção contra MIME sniffing
- `Referrer-Policy`
- `Permissions-Policy`

**Correção:**
```java
// SecurityConfig.java
.headers(headers -> headers
    .frameOptions(frame -> frame.sameOrigin())
    .httpStrictTransportSecurity(hsts -> hsts
        .includeSubDomains(true)
        .maxAgeInSeconds(31536000)
        .preload(true)
    )
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; frame-ancestors 'none'; script-src 'self'")
    )
    .referrerPolicy(referrer -> referrer
        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
    )
    .permissionsPolicy(permissions -> permissions
        .policy("camera=(), microphone=(), geolocation=()")
    )
)
```

---

### [MÉDIO-02] JWT sem Mecanismo de Revogação — `JwtService.java`
**OWASP:** A07:2021 – Identification and Authentication Failures

**Problema:**
```java
// JwtService.java — token válido por 24h sem possibilidade de invalidação
@Value("${security.jwt.expiration}")
private long jwtExpiration;  // 86400000ms = 24 horas
```

Uma vez emitido, o token JWT **não pode ser invalidado** antes de expirar. Se um token for roubado (token hijacking), o atacante tem acesso por até 24 horas mesmo que o usuário troque a senha ou seja desativado.

**Agravante:** `isAccountNonLocked()` e `isEnabled()` verificam `usuario.isAtivo()`, mas o `JwtFilter` **não recarrega o usuário do banco a cada requisição** — ele apenas valida a assinatura e a expiração do token. Um usuário desativado com token válido ainda consegue fazer requisições.

**Correção — Opção 1: Reduzir expiração + Refresh Token:**
```java
// application.properties
security.jwt.expiration=900000          # 15 minutos (access token)
security.jwt.refresh-expiration=604800000  # 7 dias (refresh token)
```

```java
// JwtService.java — adicionar geração de refresh token
public String generateRefreshToken(UserDetails userDetails) {
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
        .claim("type", "refresh")
        .signWith(getSignKey())
        .compact();
}
```

**Correção — Opção 2: Blocklist de tokens revogados (Redis):**
```java
// TokenBlocklistService.java
@Service
public class TokenBlocklistService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void revokeToken(String token, long expirationMs) {
        redisTemplate.opsForValue()
            .set("revoked:" + token, "true", expirationMs, TimeUnit.MILLISECONDS);
    }

    public boolean isRevoked(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("revoked:" + token));
    }
}

// JwtFilter.java — verificar blocklist
if (tokenBlocklistService.isRevoked(jwt)) {
    filterChain.doFilter(request, response);
    return;
}
```

**Correção — Opção 3 (mínima, sem Redis): Recarregar usuário do banco:**
```java
// JwtFilter.java — verificar status do usuário a cada requisição
if (jwtService.isTokenValid(jwt, userDetails)) {
    // Verificar se conta ainda está ativa
    if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
        filterChain.doFilter(request, response);
        return;
    }
    // ... setar autenticação
}
```
> ⚠️ Isso já acontece parcialmente pois `loadUserByUsername` é chamado, mas a verificação de `isEnabled()` não é feita explicitamente no filtro.

---

### [MÉDIO-03] Broken Access Control em `/clientes/{id}` e `/usuarios/{id}` — Controllers
**OWASP:** A01:2021 – Broken Access Control

**Problema:**
```java
// ClienteController.java
@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
@PutMapping(value = "/{id}")
public ResponseEntity<ClienteMinDTO> updateCliente(@PathVariable Long id, ...) {
    return ResponseEntity.ok(service.updateCliente(id, request));
}
```

Um usuário com role `CLIENTE` pode atualizar **qualquer cliente** passando um `id` diferente do seu. Não há verificação se o `id` da URL pertence ao usuário autenticado. O mesmo problema existe em `DELETE /clientes/{id}` e `PUT /usuarios/{id}`.

**Correção:**
```java
// ClienteController.java — verificar ownership
@PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
@PutMapping(value = "/{id}")
public ResponseEntity<ClienteMinDTO> updateCliente(
        @PathVariable Long id,
        @Valid @RequestBody ClienteRequest request,
        @AuthenticationPrincipal CustomUserDetails currentUser) {

    // Verificar se o cliente pertence ao usuário autenticado (ou é ADMIN)
    clienteService.validateOwnership(id, currentUser);
    return ResponseEntity.ok(service.updateCliente(id, request));
}
```

```java
// ClienteService.java
public void validateOwnership(Long clienteId, CustomUserDetails currentUser) {
    boolean isAdmin = currentUser.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    
    if (!isAdmin) {
        Cliente cliente = repository.findById(clienteId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
        
        if (!cliente.getUsuario().getUsername().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("Acesso negado: você não pode modificar este recurso");
        }
    }
}
```

**Alternativa com `@PreAuthorize` e SpEL:**
```java
@PreAuthorize("hasRole('ADMIN') or @clienteService.isOwner(#id, authentication.name)")
@PutMapping(value = "/{id}")
public ResponseEntity<ClienteMinDTO> updateCliente(@PathVariable Long id, ...) { ... }
```

---

### [MÉDIO-04] `SecurityService.isAdmin()` com Role Hardcoded Inconsistente
**OWASP:** A01:2021 – Broken Access Control

**Problema:**
```java
// SecurityService.java
public boolean isAdmin() {
    return SecurityContextHolder.getContext().getAuthentication()
        .getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));  // ← string hardcoded
}
```

A role está hardcoded como `"ROLE_ADMIN"` em vez de usar a constante `Perfil.ADMIN`. Se o nome da role mudar, este método silenciosamente deixará de funcionar. Além disso, `getLoggedUsername()` não verifica se `getAuthentication()` é `null` — pode lançar `NullPointerException` em contextos não autenticados.

**Correção:**
```java
// SecurityService.java
@Service
public class SecurityService {

    public String getLoggedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Nenhum usuário autenticado no contexto");
        }
        return auth.getName();
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals(Perfil.ADMIN)); // ← usar constante
    }

    public void validateSelfOrAdmin(String username) {
        if (!username.equals(getLoggedUsername()) && !isAdmin()) {
            throw new AccessDeniedException("Acesso negado");
        }
    }
}
```

---

### [MÉDIO-05] Swagger/OpenAPI Exposto em Produção Sem Autenticação
**OWASP:** A05:2021 – Security Misconfiguration

**Problema:**
```java
// SecurityConfig.java
.requestMatchers("/swagger-ui/**").permitAll()
.requestMatchers("/api-fabricapins/**").permitAll()
.requestMatchers("/v3/api-docs/**").permitAll()
```

```properties
# application-prod.properties
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
```

O Swagger está habilitado e público em produção. Isso expõe toda a documentação da API (endpoints, parâmetros, schemas, exemplos) para qualquer pessoa, facilitando o reconhecimento da aplicação por atacantes.

**Correção:**
```properties
# application-prod.properties — desabilitar em produção
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false
```

```java
// SecurityConfig.java — se mantiver o Swagger em prod, proteger com autenticação
.requestMatchers("/swagger-ui/**").hasRole("ADMIN")
.requestMatchers("/api-fabricapins/**").hasRole("ADMIN")
.requestMatchers("/v3/api-docs/**").hasRole("ADMIN")
```

---

### [MÉDIO-06] `server.error.include-message=always` em Produção
**OWASP:** A05:2021 – Security Misconfiguration

**Problema:**
```properties
# application-prod.properties
server.error.include-message=always   # ← expõe mensagens de erro internas
```

Mensagens de erro detalhadas podem vazar informações sobre a estrutura interna da aplicação, nomes de classes, queries SQL, stack traces, etc.

**Correção:**
```properties
# application-prod.properties
server.error.include-message=never
server.error.include-stacktrace=never
server.error.include-binding-errors=never
server.error.include-exception=false
```

Usar um `@ControllerAdvice` para retornar mensagens de erro padronizadas e seguras (o projeto já tem `CustomError`, o que é bom — garantir que ele seja usado em todos os casos).

---

## 🟢 Vulnerabilidades de Risco BAIXO

---

### [BAIXO-01] Injeção de Dependência via `@Autowired` em vez de Constructor Injection
**Risco:** Baixo (manutenibilidade e testabilidade)

**Problema:**
```java
// SecurityConfig.java, JwtFilter.java, AuthController.java
@Autowired
private JwtFilter jwtFilter;
@Autowired
private JwtService jwtService;
```

Field injection com `@Autowired` dificulta testes unitários e pode ocultar dependências circulares. O projeto já usa `@RequiredArgsConstructor` (Lombok) em alguns lugares, mas não nos arquivos de segurança.

**Correção:**
```java
// SecurityConfig.java — usar constructor injection
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    // ...
}
```

---

### [BAIXO-02] `@Bean` Desnecessário em `JwtFilter`
**Risco:** Baixo (possível double-registration do filtro)

**Problema:**
```java
// JwtFilter.java
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Bean  // ← @Bean dentro de um @Component é incorreto e pode causar registro duplo
    // ...
}
```

A anotação `@Bean` dentro de um `@Component` não tem o comportamento esperado e pode causar o registro duplo do filtro na cadeia de filtros do Spring Security.

**Correção:**
```java
// JwtFilter.java — remover @Bean
@Component
public class JwtFilter extends OncePerRequestFilter {
    // Sem @Bean aqui
}
```

---

### [BAIXO-03] Propriedade JWT com Nome Inconsistente
**Risco:** Baixo (pode causar falha silenciosa)

**Problema:**
```properties
# application.properties
security.jwt.secret_key=${FABRICAPINS_JWT_SECRET}   # ← underscore
```

```java
// JwtService.java
@Value("${security.jwt.secret-key}")   # ← hífen
private String secretKey;
```

O Spring Boot faz relaxed binding entre `secret_key` e `secret-key`, mas é uma inconsistência que pode causar confusão e erros em alguns contextos.

**Correção:**
```properties
# application.properties — padronizar com hífen (convenção Spring Boot)
security.jwt.secret-key=${FABRICAPINS_JWT_SECRET}
security.jwt.expiration=86400000
```

---

## ✅ O que está Correto e Por Quê

---

### ✅ BCrypt para Hash de Senhas
```java
// SecurityConfig.java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// UsuarioService.java
String encryptedPassword = passwordEncoder.encode(request.password());
```
**Por que está seguro:** BCrypt é o algoritmo recomendado para hash de senhas. Ele inclui salt automático, é computacionalmente custoso (resistente a brute force) e é o padrão da indústria. O custo padrão (10) é adequado.

---

### ✅ JWT Stateless com Sessão STATELESS
```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```
**Por que está seguro:** Sem sessões no servidor, não há risco de Session Fixation ou Session Hijacking via cookies de sessão. Cada requisição é autenticada pelo token JWT.

---

### ✅ Validação de Input com Bean Validation
```java
// LoginRequestDTO.java
public record LoginRequestDTO(
    @NotBlank @Size(min = 3, max = 30) String username,
    @NotBlank @Size(min = 8, max = 100) String password
) {}
```
**Por que está seguro:** Limita o tamanho dos inputs, prevenindo ataques de buffer overflow e reduzindo a superfície de ataque. O uso de `@Valid` nos controllers garante que a validação seja executada.

---

### ✅ Roles com Prefixo `ROLE_` Correto
```java
// Perfil.java
public static final String ADMIN   = "ROLE_ADMIN";
public static final String GERENTE = "ROLE_GERENTE";
public static final String VENDEDOR = "ROLE_VENDEDOR";
public static final String CLIENTE = "ROLE_CLIENTE";
```
**Por que está seguro:** O Spring Security espera o prefixo `ROLE_` para o método `hasRole()`. Usar constantes evita erros de digitação e centraliza a definição das roles.

---

### ✅ `@EnableMethodSecurity` com `@PreAuthorize`
```java
@EnableMethodSecurity  // habilita @PreAuthorize, @PostAuthorize, @Secured
```
**Por que está seguro:** Defense in depth — mesmo que uma rota passe pelo `SecurityFilterChain`, o `@PreAuthorize` no método do controller adiciona uma segunda camada de verificação de autorização.

---

### ✅ Soft Delete de Usuários
```java
// UsuarioService.java
public void deleteUsuario(Long id) {
    entity.setAtivo(false);  // soft delete
}

// CustomUserDetails.java
public boolean isEnabled() { return usuario.isAtivo(); }
public boolean isAccountNonLocked() { return usuario.isAtivo(); }
```
**Por que está seguro:** Usuários desativados não conseguem autenticar. O Spring Security verifica `isEnabled()` e `isAccountNonLocked()` durante a autenticação.

---

### ✅ Handlers Customizados de Erro de Segurança
```java
// CustomAuthenticationEntryPoint.java — 401 com JSON padronizado
// CustomAccessDeniedHandler.java — 403 com JSON padronizado
```
**Por que está seguro:** Retorna respostas JSON consistentes sem vazar informações internas. Evita que o Spring Security retorne páginas HTML de erro com stack traces.

---

### ✅ Verificação de Username Duplicado no Cadastro
```java
// UsuarioService.java
if (repository.existsByUsername(request.username())) {
    throw new DatabaseException("Já existe um usuário com esse nome");
}
```
**Por que está seguro:** Previne criação de contas duplicadas. A mensagem de erro é genérica o suficiente para não facilitar enumeração de usuários (embora o endpoint de login possa ainda ser usado para isso — ver ALTO-04).

---

### ✅ JWT com Biblioteca Moderna (jjwt 0.12.5)
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
```
**Por que está seguro:** A versão 0.12.x do jjwt usa APIs modernas e seguras. O uso de `Keys.hmacShaKeyFor()` com chave derivada de Base64 garante que o algoritmo HMAC-SHA seja usado corretamente, sem risco do ataque `alg: none`.

---

## 📋 Plano de Ação Priorizado

| Prioridade | ID | Ação | Esforço |
|------------|-----|------|---------|
| 🔴 1 | ALTO-01 | Restringir CORS para origens específicas | Baixo |
| 🔴 2 | ALTO-05 | Restringir `/usuarios/**` — apenas POST público | Baixo |
| 🔴 3 | ALTO-03 | Proteger `/actuator/**` — apenas `/health` público | Baixo |
| 🔴 4 | ALTO-02 | Proteger H2 Console — apenas ADMIN em dev | Baixo |
| 🔴 5 | ALTO-04 | Implementar rate limiting no `/auth/login` | Médio |
| 🟡 6 | MÉDIO-05 | Desabilitar Swagger em produção | Baixo |
| 🟡 7 | MÉDIO-06 | Remover `include-message=always` em prod | Baixo |
| 🟡 8 | MÉDIO-01 | Adicionar headers de segurança (CSP, HSTS) | Baixo |
| 🟡 9 | MÉDIO-03 | Validar ownership em update/delete de cliente/usuário | Médio |
| 🟡 10 | MÉDIO-04 | Corrigir `SecurityService` — usar constante e null-check | Baixo |
| 🟡 11 | MÉDIO-02 | Reduzir expiração do JWT + implementar refresh token | Alto |
| 🟢 12 | BAIXO-02 | Remover `@Bean` do `JwtFilter` | Baixo |
| 🟢 13 | BAIXO-03 | Padronizar nome da propriedade JWT | Baixo |
| 🟢 14 | BAIXO-01 | Migrar para constructor injection | Baixo |

---

## 🛡️ Sugestões Adicionais de Melhoria

### 1. Auditoria de Ações (Audit Log)
```java
// AuditService.java
@Service
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    public void logAction(String action, String resource, String username) {
        log.info("[AUDIT] user={} action={} resource={} timestamp={}",
            username, action, resource, Instant.now());
    }
}
```

### 2. Logging de Tentativas de Login
```java
// AuthController.java
@PostMapping("/login")
public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO request,
                               HttpServletRequest httpRequest) {
    try {
        authenticationManager.authenticate(...);
        log.info("[AUTH] Login bem-sucedido: user={} ip={}", 
            request.username(), httpRequest.getRemoteAddr());
    } catch (BadCredentialsException e) {
        log.warn("[AUTH] Tentativa de login falha: user={} ip={}", 
            request.username(), httpRequest.getRemoteAddr());
        throw e;
    }
}
```

### 3. Validação da Força da Chave JWT
```java
// JwtService.java — validar tamanho mínimo da chave
@PostConstruct
public void validateSecretKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    if (keyBytes.length < 32) { // mínimo 256 bits para HMAC-SHA256
        throw new IllegalStateException(
            "JWT secret key deve ter pelo menos 256 bits (32 bytes em Base64)");
    }
}
```

### 4. Proteção contra Enumeração de Usuários no Login
```java
// AuthController.java — retornar sempre a mesma mensagem de erro
} catch (AuthenticationException e) {
    // NÃO diferenciar "usuário não existe" de "senha incorreta"
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
}
```

### 5. Adicionar Testes de Segurança
```java
// SecurityTest.java
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Test
    void deveRetornar401SemToken() throws Exception {
        mockMvc.perform(get("/clientes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void clienteNaoDeveAcessarRotasAdmin() throws Exception {
        mockMvc.perform(get("/usuarios")
            .header("Authorization", "Bearer " + clienteToken))
            .andExpect(status().isForbidden());
    }
}
```

---

*Auditoria gerada com base na análise estática do código-fonte. Recomenda-se complementar com testes de penetração dinâmicos (DAST) usando ferramentas como OWASP ZAP.*
