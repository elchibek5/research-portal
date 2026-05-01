package com.haritara.portal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoints for Kubernetes liveness and readiness probes
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * Liveness probe - returns 200 if application is running
     */
    @GetMapping("/live")
    public ResponseEntity<HealthStatus> liveness() {
        return ResponseEntity.ok(new HealthStatus("UP", "Service is running"));
    }

    /**
     * Readiness probe - returns 200 if application is ready to handle traffic
     */
    @GetMapping("/ready")
    public ResponseEntity<HealthStatus> readiness() {
        return ResponseEntity.ok(new HealthStatus("UP", "Service is ready for traffic"));
    }

    /**
     * Simple health status response
     */
    public record HealthStatus(String status, String message) {
    }
}

