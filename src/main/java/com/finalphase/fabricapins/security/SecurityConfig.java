package com.finalphase.fabricapins.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;


    @Bean
    @Profile("dev")
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception{
        configureCommonSettings(http)
                .authorizeHttpRequests(auth -> auth
                        // Health check para deploy no Render
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // Banco de Dados H2
                        .requestMatchers("/h2-console/**").permitAll()
                        // swagger
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api-fabricapins/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        // aplicação
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "GERENTE", "VENDEDOR")
                        .requestMatchers("/api/v1/me/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())); //impede que aplicação seja aberta dentro de um <iframe> em outro site.
        return http.build();
    }


    @Bean
    @Profile("!dev")
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception{
        configureCommonSettings(http)
                .authorizeHttpRequests(auth -> auth
                        // Health check para deploy no Render
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        // aplicação
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "GERENTE", "VENDEDOR")
                        .requestMatchers("/api/v1/me/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts   // obriga o uso de https
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                        .frameOptions(frame -> frame.deny())
                        .contentSecurityPolicy(contentSecurityPolicyConfig -> contentSecurityPolicyConfig   //define origens que podem executar scripts.
                                .policyDirectives(
                                        "default-src 'self'; frame-ancestors 'none'; object-src 'none'"
                                ))
                        .referrerPolicy(referrerPolicyConfig -> referrerPolicyConfig    // controla quais informações da URL de origem podem ser enviadas para outros sites
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .permissionsPolicy(permissionsPolicyConfig -> permissionsPolicyConfig   // restringe permissoes do navegador
                                .policy("camera=(), microphone=(), geolocation=(self)"))
                );
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }


    private HttpSecurity configureCommonSettings(HttpSecurity http) throws Exception {
         return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf((csrf -> csrf.disable()))
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }
}
