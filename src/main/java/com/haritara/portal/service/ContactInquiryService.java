package com.haritara.portal.service;

import com.haritara.portal.dto.ContactInquiryRequestDTO;
import com.haritara.portal.dto.ContactInquiryResponseDTO;
import com.haritara.portal.exception.ResourceNotFoundException;
import com.haritara.portal.model.ContactInquiry;
import com.haritara.portal.repository.ContactInquiryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ContactInquiryService {

    private final ContactInquiryRepository repository;
    private final EmailService emailService;

    public ContactInquiryService(ContactInquiryRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    public List<ContactInquiryResponseDTO> getAllInquiries() {
        log.debug("Fetching all contact inquiries");
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ContactInquiryResponseDTO getInquiryById(Long id) {
        log.debug("Fetching contact inquiry with id: {}", id);
        ContactInquiry inquiry = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactInquiry", id));
        return toResponseDTO(inquiry);
    }

    @Transactional
    public ContactInquiryResponseDTO createInquiry(ContactInquiryRequestDTO request) {
        log.info("Creating contact inquiry from: {}", request.email());
        ContactInquiry inquiry = new ContactInquiry();
        inquiry.setName(request.name());
        inquiry.setEmail(request.email());
        inquiry.setMessage(request.message());
        inquiry.setReason(request.reason());
        ContactInquiry saved = repository.save(inquiry);

        emailService.sendInquiryNotification(saved);

        return toResponseDTO(saved);
    }

    private ContactInquiryResponseDTO toResponseDTO(ContactInquiry inquiry) {
        return new ContactInquiryResponseDTO(
                inquiry.getId(),
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getMessage(),
                inquiry.getReason(),
                inquiry.getCreatedAt()
        );
    }
}
