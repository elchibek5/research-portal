package com.haritara.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlogPostRequestDTO(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Content is required")
        String content,

        @NotBlank(message = "Author is required")
        @Size(max = 100, message = "Author name must not exceed 100 characters")
        String author
) {
}
