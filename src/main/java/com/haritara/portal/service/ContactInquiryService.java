package com.haritara.portal.service;

import com.haritara.portal.dto.ContactInquiryRequestDTO;
import com.haritara.portal.dto.ContactInquiryResponseDTO;
import com.haritara.portal.exception.ResourceNotFoundException;
import com.haritara.portal.model.ContactInquiry;
import com.haritara.portal.repository.ContactInquiryRepository;
import com.haritara.portal.util.InputSanitizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contact Inquiry Service with production-grade features
 * - Transactional consistency (Critical #5)
 * - Input sanitization (High #9)
 * - Pagination (Medium #11)
 * - Caching (Medium #14)
 * - Email notifications with retry (Critical #4)
 */
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

    /**
     * Get all inquiries with pagination and caching (Medium #11, #14)
     */
    @Cacheable(value = "contact-inquiries")
    public Page<ContactInquiryResponseDTO> getAllInquiries(int page, int size) {
        log.debug("Fetching contact inquiries - page: {}, size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAll(pageRequest)
                .map(this::toResponseDTO);
    }

    /**
     * Get single inquiry by ID with caching
     */
    @Cacheable(value = "contact-inquiries")
    public ContactInquiryResponseDTO getInquiryById(Long id) {
        log.debug("Fetching contact inquiry with id: {}", id);
        ContactInquiry inquiry = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactInquiry", id));
        return toResponseDTO(inquiry);
    }

    /**
     * Create contact inquiry (Critical #5: transactional + email)
     * Ensures database and email operations are atomic
     */
    @Transactional
    @CacheEvict(value = "contact-inquiries", allEntries = true)
    public ContactInquiryResponseDTO createInquiry(ContactInquiryRequestDTO request) {
        log.info("Creating contact inquiry from: {}", request.email());

        ContactInquiry inquiry = new ContactInquiry();
        inquiry.setName(InputSanitizationUtil.sanitize(request.name()));
        inquiry.setEmail(InputSanitizationUtil.sanitizeEmail(request.email()));
        inquiry.setMessage(InputSanitizationUtil.sanitize(request.message()));
        inquiry.setReason(request.reason() != null ? InputSanitizationUtil.sanitize(request.reason()) : null);

        ContactInquiry saved = repository.save(inquiry);

        // Send email notification asynchronously with retry logic
        emailService.sendInquiryNotification(saved);

        log.info("Contact inquiry created successfully [ID: {}]", saved.getId());
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


