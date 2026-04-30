package com.haritara.portal.filter;

/**
 * Thread-local holder for correlation ID
 * Allows access to correlation ID from any service without passing it as parameter
 */
public class CorrelationIdHolder {

    private static final ThreadLocal<String> correlationId = new ThreadLocal<>();

    public static void set(String id) {
        correlationId.set(id);
    }

    public static String get() {
        return correlationId.get();
    }

    public static void clear() {
        correlationId.remove();
    }
}

