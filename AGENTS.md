# AGENTS.md - Production Backend Guide (20 Improvements Implemented)

## Project Overview

Spring Boot 4.0.3 backend for the HariTara Research Center startup (Java 21, MySQL, Maven) with **20 production-level
improvements** implemented for enterprise-grade reliability, security, and performance. Provides REST APIs for managing
research studies, blog posts, study applications, and contact inquiries.

## Architecture: Clean Architecture + Production Hardening

### Production-Critical Features (Improvements 1-5)

1. **Structured Logging with Correlation IDs** - GlobalExceptionHandler logs all exceptions with request tracing IDs for
   distributed debugging
2. **Connection Pool Tuning** - HikariCP configured: 10 dev/30 prod connections with timeouts and lifecycle management
3. **Configurable Email Sender** - Resend email address via `resend.from-email` property (no hardcoding)
4. **Email Retry with Exponential Backoff** - 3 retry attempts using Spring Retry @Retryable annotation
5. **Transactional Consistency** - All service create/update methods wrapped in @Transactional to ensure email+database
   atomicity

### High-Priority Features (Improvements 6-10)

6. **Request/Response Logging Aspect** - Logs all controller invocations with performance metrics via AOP
7. **X-Correlation-Id Request Tracing** - Filter adds unique IDs to all requests for cross-service debugging
8. **Rate Limiting (Bucket4j)** - Per-IP rate limiting (100 req/min dev, 50 prod) prevents DDoS/spam
9. **Input Sanitization (OWASP HTML Escaper)** - XSS protection on user inputs (name, message, content)
10. **Enhanced Actuator Monitoring** - Prometheus metrics, detailed health checks (health, info, metrics, prometheus
    endpoints)

### Medium-Priority Features (Improvements 11-15)

11. **Pagination Support** - All GET list endpoints: `?page=0&size=10` with Sort support (createdAt DESC default)
12. **API Versioning Strategy** - `/api/v1/*` paths; forward-compatible DTO contracts documented
13. **Database Indexes** - Strategic indexes on email, studyId, status, createdAt for query performance
14. **Spring Caching with Invalidation** - @Cacheable on reads, @CacheEvict on create; Redis ready for prod
15. **Swagger/OpenAPI Auto-Documentation** - Interactive API docs at `/swagger-ui/index.html` with @Operation
    annotations

### Low-Priority Features (Improvements 16-20)

16. **Javers Audit Trail** - Complete change history for compliance auditing (who, what, when)
17. **Async Task Processing** - ThreadPoolTaskExecutor for non-blocking operations (future email batching)
18. **Security Headers (Spring Security)** - CSRF, XSS, Clickjacking (X-Frame-Options), HSTS, CSP protection
19. **Structured Logging Ready** - Logback JSON encoder dependency ready for ELK stack integration
20. **Production Profile** - Separate application-prod.properties with stricter settings and monitoring

## Key Directories & Files

```
src/main/java/com/haritara/portal/
├── *Controller.java              # REST endpoints (@RestController, @Tag for Swagger)
├── service/*.java                # Business logic (@Transactional, @Cacheable, retry logic)
├── model/*.java                  # JPA entities with @Index, @CreatedDate, @LastModifiedDate
├── repository/*.java             # JpaRepository for data access
├── dto/*.java                    # Java Records (immutable request/response DTOs)
├── config/                       # Spring configuration
│   ├── SecurityConfig.java       # Security headers (CSRF, XSS, HSTS)
│   ├── OpenAPIConfig.java        # Swagger/OpenAPI configuration
│   ├── AsyncConfig.java          # ThreadPoolTaskExecutor bean
│   └── JaversConfig.java         # Audit trail configuration
├── aspect/RequestLoggingAspect.java    # AOP logging with perf metrics
├── filter/CorrelationIdFilter.java     # X-Correlation-Id header injection
├── interceptor/RateLimitingInterceptor.java # Bucket4j rate limiting
├── util/InputSanitizationUtil.java     # HTML escaping utility
├── exception/ResourceNotFoundException.java
├── GlobalExceptionHandler.java   # Centralized error responses with logging
├── WebConfig.java                # CORS, interceptor registration, @EnableCaching, @EnableRetry
└── HealthController.java         # Liveness/readiness probes for Kubernetes

src/main/resources/
├── application.properties        # Dev config: H2, simple cache, 100 req/min limit
├── application-prod.properties   # Prod config: MySQL validate, Redis, 50 req/min, restricted actuator
└── application-test.properties   # Test config: H2 in-memory, minimal logging
```

## Developer Workflows

### Build & Run

```bash
# Local development with hot reload
./mvnw spring-boot:run

# Production build (with optimizations)
./mvnw clean package -DskipTests -Pprod

# Run with production profile
./mvnw spring-boot:run --spring.profiles.active=prod

# Tests (uses H2 in-memory + application-test.properties)
./mvnw test

# Compile only
./mvnw clean compile
```

### Configuration & Environment Variables

```bash
# Development defaults (application.properties)
DB_URL=jdbc:h2:mem:testdb
RESEND_API_KEY=dummy-key
ADMIN_EMAIL=admin@haritara.com

# Production requirements (set these!)
DB_URL=jdbc:mysql://prod-db:3306/haritara
DB_USERNAME=ht_prod
DB_PASSWORD=<strong-password>
RESEND_API_KEY=re_xxxxx
ADMIN_EMAIL=admin@haritara.com
REDIS_URL=redis://redis-host:6379
CORS_ALLOWED_ORIGINS=https://haritara.com
```

## Project-Specific Conventions

### DTOs (Immutability via Java Records)

```java
public record BlogPostRequestDTO(
    @NotBlank String title,
    @NotBlank String content,
    @NotBlank String author
) { }
```

### Entities (with Auditing & Indexes)

```java
@Entity
@Table(indexes = {@Index(name = "idx_email", columnList = "email")})
@EntityListeners(AuditingEntityListener.class)
public class StudyApplication {
    @CreatedDate private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
}
```

### Services (Transactional + Caching + Input Sanitization)

```java

@Service
@Transactional(readOnly = true)
public class BlogService {
    @Cacheable("blog-posts")
    public Page<BlogPostResponseDTO> getAllBlogPosts(int page, int size) {
    }

    @Transactional
    @CacheEvict(value = "blog-posts", allEntries = true)
    public BlogPostResponseDTO createBlogPost(BlogPostRequestDTO req) {
        blog.setContent(InputSanitizationUtil.sanitize(req.content()));
        return toDTO(repository.save(blog));
    }
}
```

### Controllers (with Pagination, Swagger Docs, Rate Limiting)

```java
@RestController
@RequestMapping("/api/v1/blog")
@Tag(name = "Blog Posts")
public class BlogController {
    @GetMapping
    @Operation(summary = "Get blog posts with pagination")
    public ResponseEntity<Page<BlogPostResponseDTO>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.getAllBlogPosts(page, size));
    }
}
```

## Integration Points

### Email Notifications (Resend + Retry Logic)

```java
@Transactional
public StudyApplicationResponseDTO createApplication(StudyApplicationRequestDTO req) {
    StudyApplication app = repository.save(new StudyApplication(...))
    emailService.sendApplicationNotification(app);  // Retry 3x with exponential backoff
    return toDTO(app);
}
```

### Request Tracing (X-Correlation-Id)

- Injected by `CorrelationIdFilter` on all requests
- Logged by `RequestLoggingAspect` and `GlobalExceptionHandler`
- Returned in response headers for client reference

### Monitoring & Observability

- **Health**: `/actuator/health` (liveness & readiness probes)
- **Metrics**: `/actuator/prometheus` (Prometheus scrape endpoint)
- **API Docs**: `/swagger-ui/index.html` (interactive Swagger UI)

### Database & Caching

- **Dev**: H2 in-memory + Simple in-memory cache
- **Prod**: MySQL + Redis distributed cache with fallback to simple
- **Indexes**: email, studyId, status, createdAt on all relevant tables

### Frontend Integration

- **CORS**: Enabled for `http://localhost:5173` (React dev) / `https://haritara.com` (prod)
- **Response Headers**: Include `X-Correlation-Id` and `X-Rate-Limit-Remaining`
- **Error Format**: Standardized JSON with status, error, message, correlationId

## Deployment Checklist for Production

- [ ] Set all required environment variables (DB_URL, RESEND_API_KEY, ADMIN_EMAIL, REDIS_URL)
- [ ] Run database migrations (DDL auto-generation disabled in prod)
- [ ] Configure production CORS origins (`cors.allowed-origins`)
- [ ] Adjust rate limiting if needed (`app.rate-limit.requests-per-minute=50`)
- [ ] Set up Prometheus/Grafana dashboards pointing to `/actuator/prometheus`
- [ ] Enable structured JSON logging via Logback for ELK integration
- [ ] Configure daily database backups with point-in-time recovery
- [ ] Set up alerts for high error rates, slow queries, and connectivity issues
- [ ] Test email notifications with production Resend API key
- [ ] Verify Redis connectivity for distributed caching
- [ ] Load test with k6/JMeter to validate rate limiting and connection pool settings

## Common Patterns & Best Practices

### Error Handling

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    String correlationId = getCorrelationId();
    log.error("Unexpected Exception [{}]", correlationId, ex);
    return ResponseEntity.status(500)
        .body(ErrorResponse.of(500, "Internal Server Error", "...", correlationId));
}
```

### Request Logging with Performance

```java
@Around("execution(* com.haritara.portal.*Controller.*(..))")
public Object logControllerResponseTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    try {
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        log.info("Controller Response [{}] - Duration: {}ms - Status: SUCCESS", 
                 correlationId, duration);
        return result;
    } catch (Exception e) {
        long duration = System.currentTimeMillis() - startTime;
        log.error("Controller Response [{}] - Duration: {}ms - ERROR: {}", 
                  correlationId, duration, e.getMessage(), e);
        throw e;
    }
}
```

## Troubleshooting

### Email Notifications Failing

- Check logs for retry attempts: "Failed to send email after 3 retries for..."
- Verify `RESEND_API_KEY` is valid via Resend dashboard
- Ensure `ADMIN_EMAIL` is configured and valid

### Rate Limit Errors (429 Too Many Requests)

- Adjust `app.rate-limit.requests-per-minute` in properties
- Check if client is properly using `Retry-After` header in 429 responses
- Verify X-Forwarded-For header handling for load balancers

### Slow Query Performance

- Check database indexes exist: `SHOW INDEX FROM studies, blog_posts, study_applications, contact_inquiries`
- Enable query logging: `spring.jpa.show-sql=true` (dev only)
- Review cache hit rates via `/actuator/prometheus` (cache_* metrics)

### High Memory Usage (Java Heap)

- Reduce HikariCP pool: `spring.datasource.hikari.maximum-pool-size`
- Increase JVM heap if needed: `-Xmx1g` flag
- Verify Redis connection is working (distributed cache fallback)

## Version Strategy & Backward Compatibility

- **API Path**: `/api/v1/*` (future versions: `/api/v2/*`)
- **DTO Stability**: Only add fields to response DTOs; never remove
- **Deprecation Process**: Add `@Deprecated` annotation before removing endpoints
- **Documentation**: Update Swagger with `@Deprecated` and migration guidance

---

**Status**: ✅ All 20 Production Improvements Implemented  
**Last Updated**: April 2026  
**Maintainers**: HariTara Engineering Team
