package com.haritara.portal.util;

import org.owasp.encoder.Encode;

/**
 * Input sanitization utility for preventing XSS attacks (High #9)
 * Uses OWASP HTML escaper to safely encode user inputs
 */
public class InputSanitizationUtil {

    private InputSanitizationUtil() {
        // Utility class - cannot be instantiated
    }

    /**
     * Sanitize string input to prevent XSS attacks
     *
     * @param input Raw user input
     * @return HTML-escaped safe string
     */
    public static String sanitize(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        return Encode.forHtml(input);
    }

    /**
     * Sanitize email input (minimal escaping)
     *
     * @param email Raw email address
     * @return Trimmed and lowercase email
     */
    public static String sanitizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        return email.trim().toLowerCase();
    }

    /**
     * Sanitize phone number (remove special characters except common separators)
     *
     * @param phone Raw phone number
     * @return Cleaned phone number
     */
    public static String sanitizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return phone;
        }
        // Keep only digits, spaces, hyphens, parentheses, and plus
        return phone.replaceAll("[^0-9\\s\\-()\\+]", "");
    }
}

