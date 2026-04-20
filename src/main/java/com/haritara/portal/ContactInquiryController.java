package com.haritara.portal;

import com.haritara.portal.dto.ContactInquiryRequestDTO;
import com.haritara.portal.dto.ContactInquiryResponseDTO;
import com.haritara.portal.service.ContactInquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inquiries")
public class ContactInquiryController {

    private final ContactInquiryService inquiryService;

    public ContactInquiryController(ContactInquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @GetMapping
    public ResponseEntity<List<ContactInquiryResponseDTO>> getAllInquiries() {
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactInquiryResponseDTO> getInquiryById(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.getInquiryById(id));
    }

    @PostMapping
    public ResponseEntity<ContactInquiryResponseDTO> createInquiry(
            @Valid @RequestBody ContactInquiryRequestDTO request) {
        ContactInquiryResponseDTO response = inquiryService.createInquiry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
