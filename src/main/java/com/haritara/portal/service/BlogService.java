package com.haritara.portal.service;

import com.haritara.portal.dto.BlogPostRequestDTO;
import com.haritara.portal.dto.BlogPostResponseDTO;
import com.haritara.portal.exception.ResourceNotFoundException;
import com.haritara.portal.model.BlogPost;
import com.haritara.portal.repository.BlogPostRepository;
import lombok.extern.slf4j.Slf4j;
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

    public List<BlogPostResponseDTO> getAllBlogPosts() {
        log.debug("Fetching all blog posts");
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public BlogPostResponseDTO getBlogPostById(Long id) {
        log.debug("Fetching blog post with id: {}", id);
        BlogPost post = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", id));
        return toResponseDTO(post);
    }

    @Transactional
    public BlogPostResponseDTO createBlogPost(BlogPostRequestDTO request) {
        log.info("Creating blog post: {}", request.title());
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(request.title());
        blogPost.setContent(request.content());
        blogPost.setAuthor(request.author());
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
