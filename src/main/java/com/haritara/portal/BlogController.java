package com.haritara.portal;

import com.haritara.portal.dto.BlogPostRequestDTO;
import com.haritara.portal.dto.BlogPostResponseDTO;
import com.haritara.portal.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    /**
     * Get all blog posts with pagination support (Medium #11)
     */
    @GetMapping
    public ResponseEntity<Page<BlogPostResponseDTO>> getAllBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(blogService.getAllBlogPosts(page, size));
    }

    /**
     * Get single blog post by ID (Medium #14: with caching)
     */
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostResponseDTO> getBlogPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getBlogPostById(id));
    }

    /**
     * Create new blog post (High #9: with input sanitization)
     */
    @PostMapping
    public ResponseEntity<BlogPostResponseDTO> createBlogPost(@Valid @RequestBody BlogPostRequestDTO request) {
        BlogPostResponseDTO response = blogService.createBlogPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
