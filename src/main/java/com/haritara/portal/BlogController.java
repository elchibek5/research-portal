package com.haritara.portal;

import com.haritara.portal.dto.BlogPostRequestDTO;
import com.haritara.portal.dto.BlogPostResponseDTO;
import com.haritara.portal.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Blog Controller - Rest endpoints for managing blog posts (Multiple improvements)
 * - Paginated GET endpoints (Medium #11)
 * - API versioning (/api/v1/*) (Medium #12)
 * - Caching (Medium #14)
 * - Swagger documentation (Medium #15)
 */
@RestController
@RequestMapping("/api/v1/blog")
@Tag(name = "Blog Posts", description = "Manage research blog posts and updates")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    /**
     * Get all blog posts with pagination support (Medium #11)
     */
    @GetMapping
    @Operation(summary = "Get blog posts with pagination", description = "Retrieve all blog posts with pagination support")
    public ResponseEntity<Page<BlogPostResponseDTO>> getAllBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.getAllBlogPosts(page, size));
    }

    /**
     * Get single blog post by ID (Medium #14: with caching)
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get blog post by ID", description = "Retrieve a specific blog post")
    public ResponseEntity<BlogPostResponseDTO> getBlogPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getBlogPostById(id));
    }

    /**
     * Create new blog post (High #9: with input sanitization)
     */
    @PostMapping
    @Operation(summary = "Create blog post", description = "Create a new blog post with sanitized content")
    public ResponseEntity<BlogPostResponseDTO> createBlogPost(@Valid @RequestBody BlogPostRequestDTO request) {
        BlogPostResponseDTO response = blogService.createBlogPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


