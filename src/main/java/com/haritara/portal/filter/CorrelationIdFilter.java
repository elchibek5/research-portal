package com.haritara.portal.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Correlation ID filter for request tracing (High #7)
 * Adds unique X-Correlation-Id header to all requests for cross-service debugging
 */
@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        // Generate new ID if not present in header
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = generateCorrelationId();
        }

        // Add to response headers for client reference
        response.addHeader(CORRELATION_ID_HEADER, correlationId);

        // Store in thread-local for use in services/controllers
        CorrelationIdHolder.set(correlationId);

        try {
            log.debug("Incoming request [{}] {} {}", correlationId, request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            CorrelationIdHolder.clear();
        }
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}

