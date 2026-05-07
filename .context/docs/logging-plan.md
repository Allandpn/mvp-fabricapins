Perfeito! Analisei os componentes críticos da aplicação. Agora vou criar o plano completo de implementação de logging e auditoria.

---

# 📋 PLANO DE IMPLEMENTAÇÃO - LOGGING E AUDITORIA

## 📌 Análise da Arquitetura Atual

### ✅ **Pontos Positivos Identificados**
- Spring Boot 3.3.5 com Spring Security moderno
- JWT implementado corretamente
- Handlers de exceção centralizados
- Rate limiting com Bucket4j
- Actuator configurado
- Separação dev/prod
- Custom handlers de autenticação e autorização

### ⚠️ **Gaps Críticos Identificados**
1. **Nenhuma configuração de logging estruturado**
2. **Ausência de auditoria de eventos de segurança**
3. **Falta de correlation ID para rastreamento**
4. **Sem logs estruturados nos pontos críticos**
5. **Ausência de sanitização de dados sensíveis**
6. **Nenhuma persistência de eventos de auditoria**

---

## 🎯 Diferença: Logging Técnico vs Auditoria

### **Logging Técnico**
- **Objetivo**: Debug, troubleshooting, performance
- **Destino**: Arquivos de log, console
- **Formato**: Mensagens técnicas
- **Exemplos**: 
  - Erros de sistema
  - Performance queries
  - Exceptions stacktraces
  - Integração externa falhando

### **Auditoria de Segurança**
- **Objetivo**: Compliance, forense, rastreabilidade de ações
- **Destino**: Banco de dados
- **Formato**: Estruturado, imutável
- **Exemplos**:
  - Login sucesso/falha
  - Operações CRUD em dados sensíveis
  - Mudanças de permissão
  - Acesso negado
  - Ações administrativas

---

## 🏗️ Arquitetura Proposta

### **Estrutura de Pacotes**

```
com.finalphase.fabricapins/
├── security/
│   ├── (existente)
│   └── audit/
│       ├── SecurityAuditService.java
│       └── SecurityEvent.java
├── audit/
│   ├── domain/
│   │   ├── AuditLog.java (Entity)
│   │   └── AuditEventType.java (Enum)
│   ├── repository/
│   │   └── AuditLogRepository.java
│   ├── service/
│   │   └── AuditService.java
│   └── dto/
│       └── AuditEventDTO.java
├── logging/
│   ├── filter/
│   │   └── RequestLoggingFilter.java
│   ├── aspect/
│   │   └── ServiceLoggingAspect.java (OPCIONAL - Fase 3)
│   └── config/
│       └── LoggingConfig.java
└── config/
    └── (existente)
```

---

## 📊 Estratégia de Persistência

### **O que vai para BANCO (Auditoria)**
✅ Login sucesso/falha  
✅ Access denied  
✅ Token inválido  
✅ Ações administrativas (CRUD Usuario, Perfil)  
✅ Operações destrutivas (DELETE)  
✅ Mudanças em pedidos (status, valor)  
✅ Aplicação de cupons  

### **O que vai para ARQUIVO (Logging Técnico)**
✅ Erros de sistema  
✅ Exceptions não tratadas  
✅ Performance issues  
✅ Integração externa (MelhorEnvio)  
✅ Rate limiting triggered  
✅ Validations failures  

### **O que vai para CONSOLE (Dev)**
✅ Tudo (dev environment)  

---

## 🔒 Eventos Obrigatórios para MVP

### **Prioridade CRÍTICA**

| Evento | Tipo | Destino | Dados |
|--------|------|---------|-------|
| Login sucesso | AUDIT | DB | username, IP, timestamp |
| Login falha | AUDIT | DB | username, IP, timestamp, motivo |
| Access denied | AUDIT | DB | username, endpoint, método, timestamp |
| Token inválido | AUDIT | DB + LOG | IP, endpoint, timestamp |
| Rate limit exceeded | LOG | FILE | IP, endpoint, timestamp |
| Usuario criado | AUDIT | DB | admin, usuario_criado, perfis |
| Usuario deletado | AUDIT | DB | admin, usuario_deletado |
| Pedido criado | AUDIT | DB | cliente, valor, itens_count |
| Integração falhou | LOG | FILE | endpoint, error, response |

### **Prioridade ALTA (Fase 2)**

| Evento | Tipo | Destino |
|--------|------|---------|
| Perfil alterado | AUDIT | DB |
| Senha alterada | AUDIT | DB |
| Estoque modificado | AUDIT | DB |
| Cupom aplicado | AUDIT | DB |

---

## 📝 Implementação: Logback Configuration

### **Criar: `src/main/resources/logback-spring.xml`**

```xml logback-spring.xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!-- PROPERTIES -->
    <springProperty scope="context" name="APP_NAME" source="spring.application.name"/>
    
    <!-- PATTERN -->
    <property name="CONSOLE_LOG_PATTERN" 
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{36}) - %X{correlationId} - %msg%n"/>
    
    <property name="FILE_LOG_PATTERN" 
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - correlationId=%X{correlationId} userId=%X{userId} ip=%X{clientIp} - %msg%n"/>
    
    <!-- CONSOLE APPENDER -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${CONSOLE_LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- FILE APPENDER - APPLICATION -->
    <appender name="FILE_APP" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/fabricapins-app.log</file>
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/archived/fabricapins-app-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>500MB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- FILE APPENDER - SECURITY -->
    <appender name="FILE_SECURITY" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/fabricapins-security.log</file>
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/archived/fabricapins-security-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>90</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- FILE APPENDER - ERROR -->
    <appender name="FILE_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/fabricapins-error.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>${FILE_LOG_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/archived/fabricapins-error-%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>90</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <!-- LOGGERS -->
    
    <!-- Security Logger -->
    <logger name="com.finalphase.fabricapins.security" level="INFO" additivity="false">
        <appender-ref ref="FILE_SECURITY"/>
        <appender-ref ref="CONSOLE"/>
    </logger>
    
    <!-- Audit Logger -->
    <logger name="com.finalphase.fabricapins.audit" level="INFO" additivity="false">
        <appender-ref ref="FILE_SECURITY"/>
        <appender-ref ref="CONSOLE"/>
    </logger>
    
    <!-- Application Logger -->
    <logger name="com.finalphase.fabricapins" level="INFO" additivity="false">
        <appender-ref ref="FILE_APP"/>
        <appender-ref ref="FILE_ERROR"/>
        <appender-ref ref="CONSOLE"/>
    </logger>
    
    <!-- Spring Framework -->
    <logger name="org.springframework" level="INFO"/>
    <logger name="org.springframework.security" level="DEBUG"/>
    
    <!-- Hibernate -->
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE"/>
    
    <!-- ROOT -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE_APP"/>
        <appender-ref ref="FILE_ERROR"/>
    </root>
    
    <!-- PROFILE SPECIFIC -->
    <springProfile name="dev">
        <logger name="com.finalphase.fabricapins" level="DEBUG"/>
    </springProfile>
    
    <springProfile name="prod">
        <logger name="org.hibernate.SQL" level="WARN"/>
        <logger name="org.springframework" level="WARN"/>
    </springProfile>
    
</configuration>
```

---

## 🔐 Segurança dos Logs

### **❌ NUNCA LOGAR**

```java
// NÃO FAZER:
log.info("Login attempt with password: {}", password); // ❌
log.info("JWT Token: {}", token); // ❌
log.debug("Authorization header: {}", authHeader); // ❌
log.info("User data: {}", userDTO); // ❌ se contém CPF, email
```

### **✅ LOGAR COM SANITIZAÇÃO**

```java
// FAZER:
log.info("Login attempt for user: {}", username); // ✅
log.info("Token validation failed for user: {}", username); // ✅
log.debug("Auth header present: {}", authHeader != null); // ✅
log.info("User created: id={}, username={}", id, username); // ✅
```

### **Dados Sensíveis a Proteger**
- ❌ Senhas (plain ou hash parcial)
- ❌ Tokens JWT completos
- ❌ Headers Authorization completos
- ❌ CPF, RG (logar mascarado: `***.***.***-12`)
- ❌ Emails completos (logar mascarado: `a****@gmail.com`)
- ❌ Números de cartão
- ❌ Dados de frete completos

---

## 🔗 Correlation ID / Request ID

### **Por que?**
- Rastrear requisição completa (entrada → processamento → resposta)
- Correlacionar logs distribuídos
- Facilitar troubleshooting

### **Implementação: RequestLoggingFilter**

```java java RequestLoggingFilter.java
package com.finalphase.fabricapins.logging.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1) // Primeiro filtro
public class RequestLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";
    private static final String USER_ID_MDC_KEY = "userId";
    private static final String CLIENT_IP_MDC_KEY = "clientIp";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // Gera ou extrai Correlation ID
            String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }
            
            // Adiciona ao MDC (disponível em todos os logs)
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            MDC.put(CLIENT_IP_MDC_KEY, getClientIp(httpRequest));
            
            // Adiciona ao response header
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
            
            // Log da requisição
            long startTime = System.currentTimeMillis();
            log.info("Request started: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
            
            chain.doFilter(request, response);
            
            // Log da resposta
            long duration = System.currentTimeMillis() - startTime;
            log.info("Request completed: {} {} - Status: {} - Duration: {}ms", 
                httpRequest.getMethod(), 
                httpRequest.getRequestURI(),
                httpResponse.getStatus(),
                duration);
            
        } finally {
            // Limpa MDC
            MDC.clear();
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
```

---

## 📦 Implementação: Auditoria em Banco de Dados

### **1. Entity: AuditLog**

```java java AuditLog.java
package com.finalphase.fabricapins.audit.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "tb_audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditEventType eventType;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String ipAddress;
    
    private String endpoint;
    
    private String httpMethod;
    
    @Column(length = 2000)
    private String details;
    
    private String entityId;
    
    private String entityType;
    
    @Column(nullable = false)
    private Instant timestamp;
    
    private Boolean success;
    
    private String correlationId;
    
    // Construtor de conveniência
    public static AuditLog of(AuditEventType eventType, String username, String ipAddress) {
        AuditLog audit = new AuditLog();
        audit.setEventType(eventType);
        audit.setUsername(username);
        audit.setIpAddress(ipAddress);
        audit.setTimestamp(Instant.now());
        audit.setSuccess(true);
        return audit;
    }
}
```

### **2. Enum: AuditEventType**

```java java AuditEventType.java
package com.finalphase.fabricapins.audit.domain;

public enum AuditEventType {
    // Autenticação
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGOUT,
    TOKEN_INVALID,
    
    // Autorização
    ACCESS_DENIED,
    
    // Usuários
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED,
    PASSWORD_CHANGED,
    
    // Perfis
    ROLE_ASSIGNED,
    ROLE_REMOVED,
    
    // Pedidos
    ORDER_CREATED,
    ORDER_STATUS_CHANGED,
    ORDER_CANCELLED,
    
    // Admin
    ADMIN_ACTION,
    
    // Estoque
    STOCK_ADJUSTED,
    
    // Sistema
    RATE_LIMIT_EXCEEDED,
    INTEGRATION_ERROR
}
```

### **3. Repository**

```java java AuditLogRepository.java
package com.finalphase.fabricapins.audit.repository;

import com.finalphase.fabricapins.audit.domain.AuditLog;
import com.finalphase.fabricapins.audit.domain.AuditEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);
    
    List<AuditLog> findByEventTypeOrderByTimestampDesc(AuditEventType eventType);
    
    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    List<AuditLog> findByTimestampBetween(Instant start, Instant end);
    
    List<AuditLog> findByUsernameAndEventTypeOrderByTimestampDesc(String username, AuditEventType eventType);
}
```

### **4. Service**

```java java AuditService.java
package com.finalphase.fabricapins.audit.service;

import com.finalphase.fabricapins.audit.domain.AuditEventType;
import com.finalphase.fabricapins.audit.domain.AuditLog;
import com.finalphase.fabricapins.audit.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por registrar eventos de auditoria no banco de dados.
 * Os registros são persistidos de forma assíncrona para não impactar performance.
 */
@Service
public class AuditService {
    
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    
    @Autowired
    private AuditLogRepository repository;
    
    /**
     * Registra um evento de auditoria de forma assíncrona.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEvent(AuditEventType eventType, String username, String ipAddress, String details) {
        try {
            AuditLog audit = AuditLog.of(eventType, username, ipAddress);
            audit.setDetails(details);
            audit.setCorrelationId(MDC.get("correlationId"));
            
            repository.save(audit);
            
            log.info("Audit event recorded: {} - User: {} - IP: {}", 
                eventType, username, ipAddress);
                
        } catch (Exception e) {
            // Não pode falhar a operação principal por erro de auditoria
            log.error("Failed to save audit log: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Registra evento de auditoria com informações de entidade.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logEntityEvent(AuditEventType eventType, String username, String ipAddress, 
                               String entityType, String entityId, String details) {
        try {
            AuditLog audit = AuditLog.of(eventType, username, ipAddress);
            audit.setDetails(details);
            audit.setEntityType(entityType);
            audit.setEntityId(entityId);
            audit.setCorrelationId(MDC.get("correlationId"));
            
            repository.save(audit);
            
            log.info("Audit event recorded: {} - Entity: {}:{} - User: {}", 
                eventType, entityType, entityId, username);
                
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Registra falha de evento.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailedEvent(AuditEventType eventType, String username, String ipAddress, 
                               String reason) {
        try {
            AuditLog audit = AuditLog.of(eventType, username, ipAddress);
            audit.setSuccess(false);
            audit.setDetails("Falha: " + reason);
            audit.setCorrelationId(MDC.get("correlationId"));
            
            repository.save(audit);
            
            log.warn("Audit failed event recorded: {} - User: {} - Reason: {}", 
                eventType, username, reason);
                
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage(), e);
        }
    }
}
```

---

## 🔄 Integração nos Componentes Existentes

### **1. JwtFilter - Adicionar Logs**

```java java JwtFilter.java
@Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");

    if(authHeader == null || !authHeader.startsWith("Bearer ")){
        filterChain.doFilter(request, response);
        return;
    }

    final String jwt = authHeader.substring(7);
    
    try {
        final String username = jwtService.extractUsername(jwt);

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(jwtService.isTokenValid(jwt, userDetails)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                // Adiciona userId ao MDC
                MDC.put("userId", username);
                
                log.debug("User authenticated successfully: {}", username);
            } else {
                log.warn("Invalid token for user: {}", username);
            }
        }
    } catch (Exception e) {
        log.error("JWT processing error: {}", e.getMessage());
        // Não bloqueia - deixa o AuthenticationEntryPoint tratar
    }
    
    filterChain.doFilter(request, response);
}
```

### **2. CustomAuthenticationEntryPoint - Adicionar Auditoria**

```java java CustomAuthenticationEntryPoint.java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private AuditService auditService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                        AuthenticationException authException) throws IOException {
        
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String clientIp = getClientIp(request);
        String endpoint = request.getRequestURI();
        
        // Log técnico
        log.warn("Unauthorized access attempt - IP: {} - Endpoint: {} - Reason: {}", 
            clientIp, endpoint, authException.getMessage());
        
        // Auditoria
        auditService.logFailedEvent(
            AuditEventType.TOKEN_INVALID, 
            "anonymous", 
            clientIp, 
            "Endpoint: " + endpoint
        );
        
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Token inválido ou não informado",
                endpoint
        );
        
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(err));
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip != null && !ip.isBlank()) ? ip : request.getRemoteAddr();
    }
}
```

### **3. CustomAccessDeniedHandler - Adicionar Auditoria**

```java java CustomAccessDeniedHandler.java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    
    @Autowired
    private ObjectMapper mapper;
    
    @Autowired
    private AuditService auditService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                      AccessDeniedException accessDeniedException) throws IOException {
        
        HttpStatus status = HttpStatus.FORBIDDEN;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String clientIp = getClientIp(request);
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        
        // Log técnico
        log.warn("Access denied - User: {} - IP: {} - Endpoint: {} {}", 
            username, clientIp, method, endpoint);
        
        // Auditoria
        AuditLog audit = AuditLog.of(AuditEventType.ACCESS_DENIED, username, clientIp);
        audit.setEndpoint(endpoint);
        audit.setHttpMethod(method);
        audit.setDetails("Acesso negado: " + accessDeniedException.getMessage());
        audit.setSuccess(false);
        
        auditService.logEvent(AuditEventType.ACCESS_DENIED, username, clientIp,
            String.format("Endpoint: %s %s", method, endpoint));
        
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Acesso negado",
                endpoint
        );
        
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(err));
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip != null && !ip.isBlank()) ? ip : request.getRemoteAddr();
    }
}
```

### **4. AuthController - Adicionar Auditoria de Login**

```java java AuthController.java
@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Autenticação", description = "Operação de autenticação")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private AuditService auditService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request, HttpServletRequest httpRequest) {
        
        String clientIp = getClientIp(httpRequest);
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
            String jwt = jwtService.generateToken(userDetails);
            
            // Log técnico
            log.info("User logged in successfully: {}", request.username());
            
            // Auditoria
            auditService.logEvent(
                AuditEventType.LOGIN_SUCCESS, 
                request.username(), 
                clientIp,
                "Login realizado com sucesso"
            );
            
            return new LoginResponseDTO(jwt);
            
        } catch (AuthenticationException e) {
            // Log técnico
            log.warn("Failed login attempt for user: {} from IP: {}", request.username(), clientIp);
            
            // Auditoria
            auditService.logFailedEvent(
                AuditEventType.LOGIN_FAILED, 
                request.username(), 
                clientIp,
                "Credenciais inválidas"
            );
            
            throw e; // Deixa o ControllerExceptionHandler tratar
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip != null && !ip.isBlank()) ? ip : request.getRemoteAddr();
    }
}
```

### **5. UsuarioService - Adicionar Auditoria de Operações CRUD**

```java java UsuarioService.java
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository repository;
    
    @Autowired
    private AuditService auditService;
    
    // ... outros autowired

    @Transactional()
    public UsuarioDTO insertUsuario(@Valid UsuarioRequest request) {
        
        if(repository.existsByUsername(request.username())){
            throw new DatabaseException("Já existe um usuário com esse nome");
        }
        
        String encryptedPassword = passwordEncoder.encode(request.password());
        Usuario entity = new Usuario(request.username(), encryptedPassword);

        List<Perfil> perfis = perfilRepository.searchAllByName(request.perfis());
        if(perfis.size() != request.perfis().size()){
            throw new ResourceNotFoundException("Algum perfil informado não existe");
        }
        
        entity.addPerfis(perfis);
        entity = repository.save(entity);
        
        // Auditoria
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.logEntityEvent(
            AuditEventType.USER_CREATED,
            adminUsername,
            getCurrentIp(),
            "Usuario",
            entity.getId().toString(),
            String.format("Usuario criado: %s - Perfis: %s", 
                entity.getUsername(), request.perfis())
        );
        
        log.info("Usuario created: id={}, username={} by admin={}", 
            entity.getId(), entity.getUsername(), adminUsername);
        
        return mapper.toDTO(entity);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        Usuario entity = repository.findByIdAndAtivoTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario não encontrado")
        );
        
        entity.setAtivo(false);
        
        // Auditoria
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.logEntityEvent(
            AuditEventType.USER_DELETED,
            adminUsername,
            getCurrentIp(),
            "Usuario",
            id.toString(),
            String.format("Usuario deletado: %s", entity.getUsername())
        );
        
        log.warn("Usuario deleted: id={}, username={} by admin={}", 
            id, entity.getUsername(), adminUsername);
    }
    
    private String getCurrentIp() {
        // Extrai do MDC se disponível
        String ip = MDC.get("clientIp");
        return ip != null ? ip : "unknown";
    }
}
```

### **6. ControllerExceptionHandler - Adicionar Logging**

```java java ControllerExceptionHandler.java
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        
        log.warn("Resource not found: {} - Endpoint: {}", e.getMessage(), request.getRequestURI());
        
        CustomError err = new CustomError(
            Instant.now(), status.value(), status.getReasonPhrase(), 
            e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> databaseException(DatabaseException e, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        
        log.error("Database exception: {} - Endpoint: {}", e.getMessage(), request.getRequestURI());
        
        CustomError err = new CustomError(
            Instant.now(), status.value(), status.getReasonPhrase(), 
            e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomError> badCredentials(BadCredentialsException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        
        // NÃO logar a exception completa (pode conter dados sensíveis)
        log.warn("Bad credentials attempt - Endpoint: {}", request.getRequestURI());
        
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Usuário ou senha inválidos",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    // ... demais handlers com logging apropriado
}
```

---

## ⚙️ Configuração Final no application.yml

### **application.yml**

```yaml application.yml
# ... configurações existentes

# Logging Configuration
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %highlight(%-5level) %cyan(%logger{36}) - %X{correlationId} - %msg%n"
  level:
    com.finalphase.fabricapins: INFO
    com.finalphase.fabricapins.security: DEBUG
    com.finalphase.fabricapins.audit: INFO
    org.springframework.security: DEBUG

# Async Execution para auditoria
spring:
  task:
    execution:
      pool:
        core-size: 2
        max-size: 5
        queue-capacity: 100
      thread-name-prefix: audit-async-
```

### **application-dev.yml**

```yaml application-dev.yml
# ... configurações existentes

logging:
  level:
    com.finalphase.fabricapins: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
    org.springframework.security: DEBUG
```

### **application-prod.yml**

```yaml application-prod.yml
# ... configurações existentes

logging:
  level:
    com.finalphase.fabricapins: INFO
    com.finalphase.fabricapins.security: WARN
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
  file:
    name: logs/fabricapins-app.log
    max-size: 10MB
    max-history: 30
```

---

## 📊 Script de Migração: Tabela de Auditoria

### **Criar: `src/main/resources/db/migration/V2__create_audit_table.sql`**

```sql V2__create_audit_table.sql
CREATE TABLE tb_audit_log (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    username VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    endpoint VARCHAR(255),
    http_method VARCHAR(10),
    details VARCHAR(2000),
    entity_id VARCHAR(50),
    entity_type VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,
    success BOOLEAN DEFAULT TRUE,
    correlation_id VARCHAR(100)
);

-- Índices para performance
CREATE INDEX idx_audit_username ON tb_audit_log(username);
CREATE INDEX idx_audit_event_type ON tb_audit_log(event_type);
CREATE INDEX idx_audit_timestamp ON tb_audit_log(timestamp DESC);
CREATE INDEX idx_audit_correlation ON tb_audit_log(correlation_id);
CREATE INDEX idx_audit_entity ON tb_audit_log(entity_type, entity_id);

-- Comentários
COMMENT ON TABLE tb_audit_log IS 'Registro de auditoria de eventos de segurança e operações críticas';
COMMENT ON COLUMN tb_audit_log.event_type IS 'Tipo de evento auditado';
COMMENT ON COLUMN tb_audit_log.correlation_id IS 'ID de correlação para rastreamento de requisição';
```

---

## 🚀 Plano de Implementação Incremental

### **FASE 1: ESSENCIAL (MVP) - 2-3 dias**

**Prioridade CRÍTICA**

#### Checklist:

- [ ] Criar estrutura de pacotes `audit/` e `logging/`
- [ ] Implementar `logback-spring.xml`
- [ ] Criar `RequestLoggingFilter` (Correlation ID + MDC)
- [ ] Criar entidade `AuditLog` e enum `AuditEventType`
- [ ] Criar `AuditService` e `AuditLogRepository`
- [ ] Migração SQL da tabela de auditoria
- [ ] Configurar `@Async` para auditoria
- [ ] Integrar auditoria em:
  - [x] `AuthController` (login)
  - [x] `CustomAuthenticationEntryPoint`
  - [x] `CustomAccessDeniedHandler`
  - [x] `JwtFilter` (MDC userId)
- [ ] Adicionar logging em `ControllerExceptionHandler`
- [ ] Testar em DEV
- [ ] Validar rotação de logs

**Entregável**: Sistema com logging básico + auditoria de segurança

---

### **FASE 2: MELHORIAS - 2-3 dias**

**Prioridade ALTA**

#### Checklist:

- [ ] Estender auditoria para operações CRUD críticas:
  - [ ] `UsuarioService` (criar/atualizar/deletar)
  - [ ] `PedidoService` (criar/cancelar/atualizar status)
  - [ ] `PerfilService` (atribuir/remover perfis)
- [ ] Criar endpoint administrativo de consulta de auditoria:
  - [ ] `/api/v1/admin/audit/logs`
  - [ ] Filtros: usuário, tipo, período
- [ ] Implementar sanitização de logs sensíveis:
  - [ ] Criar `LogSanitizer` utility
  - [ ] Mascaramento de CPF, email
- [ ] Melhorar logs de integração externa (MelhorEnvio):
  - [ ] Log de requests/responses
  - [ ] Tratamento de falhas
- [ ] Adicionar métricas básicas:
  - [ ] Contador de logins
  - [ ] Contador de erros
- [ ] Criar dashboard simples de auditoria (opcional)
- [ ] Documentar padrões no README

**Entregável**: Auditoria completa + consulta administrativa

---

### **FASE 3: ESCALABILIDADE - 3-4 dias**

**Prioridade MÉDIA (Futuro)**

#### Checklist:

- [ ] Implementar `@Aspect` para auditoria automática:
  - [ ] `@Auditable` annotation
  - [ ] `ServiceLoggingAspect`
- [ ] Adicionar logs estruturados JSON:
  - [ ] Logstash encoder (se necessário)
  - [ ] Formato JSON padronizado
- [ ] Implementar retenção automática de logs antigos:
  - [ ] Job agendado para limpeza
  - [ ] Arquivamento em S3 (se aplicável)
- [ ] Adicionar observabilidade avançada:
  - [ ] Integração com Prometheus (métricas)
  - [ ] Health checks customizados
- [ ] Implementar alertas:
  - [ ] Múltiplas tentativas de login
  - [ ] Rate limit exceeded
  - [ ] Erros críticos
- [ ] Performance tuning:
  - [ ] Batch insert de auditoria
  - [ ] Cache de consultas frequentes
- [ ] Preparar para migração ELK (se crescer)

**Entregável**: Sistema pronto para escala + observabilidade avançada

---

## 📋 Checklist de Segurança de Logs

### **✅ O que DEVE ser feito**

- ✅ Logar eventos de autenticação (sucesso/falha)
- ✅ Logar eventos de autorização (access denied)
- ✅ Logar operações administrativas
- ✅ Logar operações destrutivas (DELETE)
- ✅ Usar Correlation ID em todas as requisições
- ✅ Adicionar contexto (userId, IP) no MDC
- ✅ Separar logs de segurança em arquivo dedicado
- ✅ Rotação e compactação de logs
- ✅ Retenção de 90 dias para logs de segurança
- ✅ Auditoria em banco de dados separado

### **❌ O que NUNCA fazer**

- ❌ Logar senhas (plain ou hash)
- ❌ Logar tokens JWT completos
- ❌ Logar headers Authorization completos
- ❌ Logar dados de cartão de crédito
- ❌ Logar CPF/RG sem mascaramento
- ❌ Logar stacktraces em produção (apenas em dev)
- ❌ Logar payloads completos com dados sensíveis
- ❌ Usar nível DEBUG em produção globalmente

---

## 🎯 Padrões Reutilizáveis

### **1. Padrão de Log Técnico**

```java
// INFO: Operação normal
log.info("Order created: id={}, customerId={}, total={}", orderId, customerId, total);

// WARN: Situação anormal mas recuperável
log.warn("Stock low for product: id={}, quantity={}", productId, quantity);

// ERROR: Erro que precisa atenção
log.error("Failed to process payment: orderId={}, error={}", orderId, e.getMessage(), e);

// DEBUG: Informação técnica detalhada (apenas dev)
log.debug("JWT validation: username={}, tokenValid={}", username, isValid);
```

### **2. Padrão de Auditoria**

```java
// Sucesso
auditService.logEntityEvent(
    AuditEventType.ORDER_CREATED,
    username,
    clientIp,
    "Pedido",
    orderId.toString(),
    String.format("Pedido criado: valor=%.2f, itens=%d", total, itemCount)
);

// Falha
auditService.logFailedEvent(
    AuditEventType.PAYMENT_FAILED,
    username,
    clientIp,
    "Gateway timeout"
);
```

### **3. Padrão de MDC**

```java
// Adicionar contexto
MDC.put("orderId", orderId.toString());
MDC.put("customerId", customerId.toString());

try {
    // operações
    log.info("Processing order"); // Incluirá orderId e customerId
} finally {
    MDC.remove("orderId");
    MDC.remove("customerId");
}
```

---

## 📖 Documentação de Referência

### **Para Desenvolvedores**

Criar: `docs/LOGGING_STANDARDS.md`

**Conteúdo**:
- Quando logar INFO vs WARN vs ERROR
- Como usar MDC
- Como adicionar auditoria em novos services
- Exemplos de código
- O que NUNCA logar

### **Para DevOps**

Criar: `docs/LOGGING_OPERATIONS.md`

**Conteúdo**:
- Localização dos arquivos de log
- Estratégia de rotação
- Como consultar logs
- Métricas importantes
- Alertas configurados

---

## 🔍 Monitoramento e Alertas (Fase 3)

### **Alertas Recomendados**

| Alerta | Condição | Ação |
|--------|----------|------|
| Múltiplas tentativas de login | > 5 falhas em 5 min do mesmo IP | Bloquear IP temporariamente |
| Rate limit excedido | > 10 ocorrências/min | Investigar bot/ataque |
| Erros de integração | > 5 falhas consecutivas | Notificar DevOps |
| Disco cheio | Logs > 90% espaço | Limpar logs antigos |
| Acesso negado frequente | > 20 access denied/hora | Investigar tentativa de invasão |

---

## 📊 Métricas Importantes

### **Auditoria**
- Total de logins/dia
- Taxa de sucesso/falha de login
- Top usuários com access denied
- Operações administrativas/dia

### **Performance**
- Duração média de requisições
- Requisições > 2s
- Requests por segundo

### **Segurança**
- Rate limits triggered
- Tokens inválidos
- Access denied por endpoint

---

## ✅ Resumo Executivo

### **O que será entregue na Fase 1 (MVP)**

1. ✅ **Logging estruturado** com Logback
2. ✅ **Correlation ID** em todas requisições
3. ✅ **MDC** com userId e clientIp
4. ✅ **Auditoria em banco** de eventos críticos
5. ✅ **Logs separados** (app, security, error)
6. ✅ **Rotação automática** de logs
7. ✅ **Segurança** (sanitização de dados sensíveis)

### **Benefícios Imediatos**

- 🔍 **Rastreabilidade completa** de requisições
- 🔒 **Auditoria de segurança** para compliance
- 🐛 **Debugging facilitado** com correlation ID
- 📊 **Visibilidade** de operações críticas
- ⚡ **Performance** (auditoria assíncrona)
- 📈 **Escalável** para crescimento futuro

### **Tempo Estimado**

- **Fase 1 (MVP)**: 2-3 dias
- **Fase 2 (Melhorias)**: 2-3 dias
- **Fase 3 (Escalabilidade)**: 3-4 dias

### **Requisitos Técnicos**

- ✅ Nenhuma dependência externa adicional
- ✅ Usa bibliotecas já presentes (Logback, SLF4J)
- ✅ Compatível com infraestrutura atual
- ✅ Sem overhead significativo de performance

---

## 📝 Conclusão

Este plano oferece uma **arquitetura pragmática e moderna** de logging e auditoria para o backend Spring Boot, focando em:

✅ **Simplicidade**: Sem overengineering  
✅ **Segurança**: Auditoria de eventos críticos  
✅ **Observabilidade**: Logs estruturados e rastreáveis  
✅ **Escalabilidade**: Preparado para crescimento  
✅ **Implementação incremental**: Valor entregue por fase  

O sistema estará **prod-ready** após Fase 1, com base sólida para evoluções futuras.

---

**Pronto para iniciar a implementação?** 

Você pode **alternar para o modo Agent** para que eu possa criar os arquivos e realizar as integrações automaticamente! 🚀