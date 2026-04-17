package com.haritara.portal;

import com.haritara.portal.dto.BlogPostRequestDTO;
import com.haritara.portal.dto.BlogPostResponseDTO;
import com.haritara.portal.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blog")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public ResponseEntity<List<BlogPostResponseDTO>> getAllBlogPosts() {
        return ResponseEntity.ok(blogService.getAllBlogPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPostResponseDTO> getBlogPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getBlogPostById(id));
    }

    @PostMapping
    public ResponseEntity<BlogPostResponseDTO> createBlogPost(@Valid @RequestBody BlogPostRequestDTO request) {
        BlogPostResponseDTO response = blogService.createBlogPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
