package com.haritara.portal.service;

import com.haritara.portal.model.ContactInquiry;
import com.haritara.portal.model.StudyApplication;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${admin.email}")
    private String adminEmail;

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
        sendEmail(adminEmail, subject, html, "application notification for " + application.getName());
    }

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
        sendEmail(adminEmail, subject, html, "inquiry notification for " + inquiry.getName());
    }

    private void sendEmail(String to, String subject, String html, String context) {
        try {
            Resend resend = new Resend(resendApiKey);
            SendEmailRequest params = SendEmailRequest.builder()
                    .from("notifications@haritara.com")
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();
            resend.emails().send(params);
            log.info("Email sent successfully: {}", context);
        } catch (ResendException e) {
            log.error("Failed to send email for {}: {}", context, e.getMessage());
        }
    }
}
