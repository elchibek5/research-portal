package com.haritara.portal.service;

import com.haritara.portal.dto.StudyResponseDTO;
import com.haritara.portal.exception.ResourceNotFoundException;
import com.haritara.portal.model.ResearchStudy;
import com.haritara.portal.repository.ResearchStudyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Research Study Service with production-grade features
 * - Transactional consistency (Critical #5)
 * - Pagination (Medium #11)
 * - Caching (Medium #14)
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ResearchStudyService {
    private final ResearchStudyRepository repository;

    public ResearchStudyService(ResearchStudyRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all studies with pagination and caching (Medium #11, #14)
     */
    @Cacheable(value = "studies")
    public Page<StudyResponseDTO> getAllStudies(int page, int size) {
        log.debug("Fetching studies - page: {}, size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAll(pageRequest)
                .map(this::toResponseDTO);
    }

    /**
     * Get single study by ID with caching
     */
    @Cacheable(value = "studies")
    public StudyResponseDTO getStudyById(Long id) {
        log.debug("Fetching study with id: {}", id);
        ResearchStudy study = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResearchStudy", id));
        return toResponseDTO(study);
    }

    private StudyResponseDTO toResponseDTO(ResearchStudy study) {
        return new StudyResponseDTO(
                study.getId(),
                study.getTitle(),
                study.getDescription(),
                study.getEligibility(),
                study.getCompensation()
        );
    }
}
