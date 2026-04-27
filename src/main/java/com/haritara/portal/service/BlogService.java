package com.haritara.portal.service;

import com.haritara.portal.dto.BlogPostRequestDTO;
import com.haritara.portal.dto.BlogPostResponseDTO;
import com.haritara.portal.exception.ResourceNotFoundException;
import com.haritara.portal.model.BlogPost;
import com.haritara.portal.repository.BlogPostRepository;
import com.haritara.portal.util.InputSanitizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BlogService {

    private final BlogPostRepository repository;

    public BlogService(BlogPostRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all blog posts with pagination (Medium #11) and caching (Medium #14)
     */
    @Cacheable(value = "blog-posts")
    public Page<BlogPostResponseDTO> getAllBlogPosts(int page, int size) {
        log.debug("Fetching blog posts - page: {}, size: {}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repository.findAll(pageRequest)
                .map(this::toResponseDTO);
    }

    /**
     * Get all blog posts as list (legacy endpoint)
     */
    public List<BlogPostResponseDTO> getAllBlogPostsList() {
        log.debug("Fetching all blog posts as list");
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    /**
     * Get single blog post by ID with caching (Medium #14)
     */
    @Cacheable(value = "blog-posts")
    public BlogPostResponseDTO getBlogPostById(Long id) {
        log.debug("Fetching blog post with id: {}", id);
        BlogPost post = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", id));
        return toResponseDTO(post);
    }

    /**
     * Create blog post with input sanitization (High #9)
     * Invalidates cache on create (Medium #14)
     */
    @Transactional
    @CacheEvict(value = "blog-posts", allEntries = true)
    public BlogPostResponseDTO createBlogPost(BlogPostRequestDTO request) {
        log.info("Creating blog post: {}", request.title());
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(InputSanitizationUtil.sanitize(request.title()));
        blogPost.setContent(InputSanitizationUtil.sanitize(request.content()));
        blogPost.setAuthor(InputSanitizationUtil.sanitize(request.author()));
        BlogPost saved = repository.save(blogPost);
        return toResponseDTO(saved);
    }

    private BlogPostResponseDTO toResponseDTO(BlogPost blogPost) {
        return new BlogPostResponseDTO(
                blogPost.getId(),
                blogPost.getTitle(),
                blogPost.getContent(),
                blogPost.getAuthor(),
                blogPost.getCreatedAt()
        );
    }
}

