package com.haritara.portal.dto;

import jakarta.validation.constraints.*;

public record StudyApplicationRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[+]?[\\d\\s\\-().]{7,20}$", message = "Phone number is invalid")
        String phone,

        @NotNull(message = "Study ID is required")
        Long studyId
) {
}
