package com.haritara.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Security configuration for production-grade security headers (Low #18)
 * Implements CSRF protection, XSS prevention, clickjacking protection, HSTS, and CSP
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure security headers and CSRF protection
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless API (only for development - enable in prod with tokens)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                // Security headers
                .headers(headers -> headers
                        // XSS Protection - enable X-XSS-Protection header
                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED))

                        // Clickjacking protection (X-Frame-Options)
                        .frameOptions(frame -> frame.deny())

                        // HSTS - forces HTTPS in production
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                                .preload(true)
                        )

                        // Content Security Policy
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'")
                        )
                )

                // Allow public access to API endpoints
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll()
                        .requestMatchers("/health/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

