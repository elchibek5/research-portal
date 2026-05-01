package com.haritara.portal.service;

import com.haritara.portal.model.ContactInquiry;
import com.haritara.portal.model.StudyApplication;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${admin.email}")
    private String adminEmail;

    /**
     * Send application notification asynchronously with retry logic (Critical #4, Low #17)
     */
    @Async("taskExecutor")
    public void sendApplicationNotification(StudyApplication application) {
        String subject = "New Study Application — " + application.getName();
        String html = """
                <h2>New Study Application Received</h2>
                <p><strong>Name:</strong> %s</p>
                <p><strong>Email:</strong> %s</p>
                <p><strong>Phone:</strong> %s</p>
                <p><strong>Study ID:</strong> %d</p>
                <p><strong>Status:</strong> %s</p>
                """.formatted(
                application.getName(),
                application.getEmail(),
                application.getPhone(),
                application.getStudyId(),
                application.getStatus()
        );
        String context = "application notification for " + application.getName();
        try {
            sendEmailWithRetry(adminEmail, subject, html, context);
        } catch (Exception e) {
            log.error("Failed to send email after max retries: {}", context, e);
        }
    }

    /**
     * Send inquiry notification asynchronously with retry logic (Critical #4, Low #17)
     */
    @Async("taskExecutor")
    public void sendInquiryNotification(ContactInquiry inquiry) {
        String subject = "New Contact Inquiry — " + inquiry.getName();
        String html = """
                <h2>New Contact Inquiry Received</h2>
                <p><strong>Name:</strong> %s</p>
                <p><strong>Email:</strong> %s</p>
                <p><strong>Reason:</strong> %s</p>
                <p><strong>Message:</strong></p>
                <p>%s</p>
                """.formatted(
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getReason() != null ? inquiry.getReason() : "N/A",
                inquiry.getMessage()
        );
        String context = "inquiry notification for " + inquiry.getName();
        try {
            sendEmailWithRetry(adminEmail, subject, html, context);
        } catch (Exception e) {
            log.error("Failed to send email after max retries: {}", context, e);
        }
    }

    /**
     * Send email with exponential backoff retry (Critical #4)
     * Max 3 attempts with increasing delay: 1s, 2s, 4s
     */
    @Retryable(
            retryFor = ResendException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    private void sendEmailWithRetry(String to, String subject, String html, String context) throws ResendException {
        try {
            Resend resend = new Resend(resendApiKey);
            SendEmailRequest params = SendEmailRequest.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();
            resend.emails().send(params);
            log.info("Email sent successfully [{}]: {}", fromEmail, context);
        } catch (ResendException e) {
            log.warn("Failed to send email (will retry): {} - Error: {}", context, e.getMessage());
            throw e;
        }
    }
}
