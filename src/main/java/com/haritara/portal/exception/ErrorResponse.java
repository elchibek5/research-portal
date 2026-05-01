package com.haritara.portal.exception;

import java.time.LocalDateTime;

/**
 * Standard error response structure for all API exceptions
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String correlationId
) {
    /**
     * Factory method to create ErrorResponse with current timestamp
     */
    public static ErrorResponse of(int status, String error, String message, String correlationId) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                error,
                message,
                correlationId
        );
    }
}

