package com.haritara.portal.dto;

public record StudyResponseDTO (
    Long id,
    String title,
    String description,
    String eligibility,
    String compensation
) {}
