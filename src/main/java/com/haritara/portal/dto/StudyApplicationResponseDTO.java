package com.haritara.portal.dto;

import com.haritara.portal.model.ApplicationStatus;

import java.time.LocalDateTime;

public record StudyApplicationResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        Long studyId,
        ApplicationStatus status,
        LocalDateTime createdAt
) {
}
