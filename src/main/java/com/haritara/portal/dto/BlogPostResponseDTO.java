package com.haritara.portal.dto;

import java.time.LocalDateTime;

public record BlogPostResponseDTO(
        Long id,
        String title,
        String content,
        String author,
        LocalDateTime createdAt
) {
}
