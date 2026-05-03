package com.haritara.portal.aspect;

import com.haritara.portal.filter.CorrelationIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Request logging aspect for controller invocations (High #6)
 * Logs all controller requests with performance metrics via AOP
 */
@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

    /**
     * Log all controller method invocations with timing
     */
    @Around("execution(* com.haritara.portal.*Controller.*(..))")
    public Object logControllerInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        String correlationId = CorrelationIdHolder.get();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            log.info("Controller [{}] {} [{}] - Duration: {}ms - Status: SUCCESS",
                    correlationId, className, methodName, duration);

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Controller [{}] {} [{}] - Duration: {}ms - ERROR: {}",
                    correlationId, className, methodName, duration, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Log all service method invocations with timing (optional)
     */
    @Around("execution(* com.haritara.portal.service.*Service.*(..))")
    public Object logServiceInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            if (duration > 1000) {
                log.warn("Slow service call [{}] {} - Duration: {}ms",
                        className, methodName, duration);
            } else {
                log.debug("Service [{}] {} - Duration: {}ms",
                        className, methodName, duration);
            }

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Service [{}] {} - Duration: {}ms - ERROR: {}",
                    className, methodName, duration, e.getMessage());
            throw e;
        }
    }
}

