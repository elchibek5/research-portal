package com.haritara.portal;

import com.haritara.portal.dto.StudyResponseDTO;
import com.haritara.portal.service.ResearchStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Research Study Controller - Rest endpoints for managing research studies (Medium #11-15)
 * - Paginated GET endpoints (Medium #11)
 * - API versioning (/api/v1/*) (Medium #12)
 * - Database indexes (Medium #13)
 * - Caching (Medium #14)
 * - Swagger documentation (Medium #15)
 */
@RestController
@RequestMapping("/api/v1/studies")
@Tag(name = "Research Studies", description = "Manage research studies and view opportunities")
public class ResearchStudyController {

    private final ResearchStudyService studyService;

    public ResearchStudyController(ResearchStudyService studyService) {
        this.studyService = studyService;
    }

    /**
     * Get all studies with pagination support (Medium #11)
     */
    @GetMapping
    @Operation(summary = "Get all research studies", description = "Retrieve all research opportunities with pagination")
    public ResponseEntity<Page<StudyResponseDTO>> getAllStudies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studyService.getAllStudies(page, size));
    }

    /**
     * Get single study by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get study by ID", description = "Retrieve a specific research study")
    public ResponseEntity<StudyResponseDTO> getStudyById(@PathVariable Long id) {
        return ResponseEntity.ok(studyService.getStudyById(id));
    }
}

