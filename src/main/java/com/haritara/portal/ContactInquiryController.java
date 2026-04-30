package com.haritara.portal;

import com.haritara.portal.dto.ContactInquiryRequestDTO;
import com.haritara.portal.dto.ContactInquiryResponseDTO;
import com.haritara.portal.service.ContactInquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contact Inquiry Controller - Rest endpoints for managing contact inquiries (Multiple improvements)
 * - Paginated GET endpoints (Medium #11)
 * - API versioning (/api/v1/*) (Medium #12)
 * - Caching (Medium #14)
 * - Swagger documentation (Medium #15)
 */
@RestController
@RequestMapping("/api/v1/inquiries")
@Tag(name = "Contact Inquiries", description = "Manage contact inquiries and requests")
public class ContactInquiryController {

    private final ContactInquiryService inquiryService;

    public ContactInquiryController(ContactInquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    /**
     * Get all inquiries with pagination support (Medium #11)
     */
    @GetMapping
    @Operation(summary = "Get all contact inquiries", description = "Retrieve all inquiries with pagination")
    public ResponseEntity<Page<ContactInquiryResponseDTO>> getAllInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(inquiryService.getAllInquiries(page, size));
    }

    /**
     * Get single inquiry by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get inquiry by ID", description = "Retrieve a specific contact inquiry")
    public ResponseEntity<ContactInquiryResponseDTO> getInquiryById(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.getInquiryById(id));
    }

    /**
     * Create contact inquiry (Critical #5: transactional + email)
     */
    @PostMapping
    @Operation(summary = "Create contact inquiry", description = "Submit a new contact inquiry with email notification")
    public ResponseEntity<ContactInquiryResponseDTO> createInquiry(
            @Valid @RequestBody ContactInquiryRequestDTO request) {
        ContactInquiryResponseDTO response = inquiryService.createInquiry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


