package com.haritara.portal;

import com.haritara.portal.model.ResearchStudy;
import com.haritara.portal.repository.ResearchStudyRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
@CrossOrigin(origins = "http://localhost:5173")

public class ResearchStudyController {

    private final ResearchStudyRepository repository;

    public ResearchStudyController(ResearchStudyRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ResearchStudy> getAllStudies() {
        return repository.findAll();
    }
}
