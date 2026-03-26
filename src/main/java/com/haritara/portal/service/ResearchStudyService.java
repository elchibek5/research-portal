package com.haritara.portal.service;

import com.haritara.portal.dto.StudyResponseDTO;
import com.haritara.portal.repository.ResearchStudyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResearchStudyService {
    private final ResearchStudyRepository repository;

    public ResearchStudyService(ResearchStudyRepository repository) {
        this.repository = repository;
    }

    public List<StudyResponseDTO> getAllStudies() {
        return repository.findAll().stream()
                .map(study -> new StudyResponseDTO(
                        study.getId(),
                        study.getTitle(),
                        study.getDescription(),
                        study.getEligibility(),
                        study.getCompensation()
                ))
                .collect(Collectors.toList());
    }
}
