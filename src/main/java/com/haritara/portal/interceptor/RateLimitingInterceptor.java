package com.haritara.portal.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting interceptor using Bucket4j (High #8)
 * Per-IP rate limiting prevents DDoS/spam attacks
 */
@Slf4j
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${app.rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!rateLimitEnabled) {
            return true;
        }

        String clientIp = getClientIp(request);
        Bucket bucket = resolveBucket(clientIp);

        if (bucket.tryConsume(1)) {
            // Request is allowed
            response.addHeader("X-Rate-Limit-Remaining", "unlimited");
            return true;
        } else {
            // Rate limit exceeded
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(429); // Too Many Requests
            response.addHeader("Retry-After", "60");
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Maximum " + requestsPerMinute + " requests per minute.\"}");
            return false;
        }
    }

    /**
     * Get or create bucket for client IP
     */
    private Bucket resolveBucket(String clientIp) {
        return cache.computeIfAbsent(clientIp, ip -> createNewBucket());
    }

    /**
     * Create new rate limit bucket
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(requestsPerMinute, Refill.intervally(requestsPerMinute, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Extract client IP from request (handles proxies)
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}

