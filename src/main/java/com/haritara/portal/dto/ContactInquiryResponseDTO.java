package com.haritara.portal.dto;

import java.time.LocalDateTime;

public record ContactInquiryResponseDTO(
        Long id,
        String name,
        String email,
        String message,
        String reason,
        LocalDateTime createdAt
) {
}
