package com.newswebsite.controller;

import com.newswebsite.dto.CommentDto;
import com.newswebsite.dto.CreateCommentRequest;
import com.newswebsite.dto.NewsDetailDto;
import com.newswebsite.dto.NewsHeadlineDto;
import com.newswebsite.model.Comment;
import com.newswebsite.model.News;
import com.newswebsite.model.User;
import com.newswebsite.repository.CommentRepository;
import com.newswebsite.repository.NewsRepository;
import com.newswebsite.repository.UserRepository;
import com.newswebsite.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get news headlines.
     * If categoryId is provided, returns headlines for that category.
     * Otherwise returns all headlines ordered by published date descending.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<NewsHeadlineDto>> getNewsHeadlines(
            @RequestParam(required = false) Long categoryId) {
        List<News> newsList;
        if (categoryId != null) {
            newsList = newsRepository.findByCategoryId(categoryId);
        } else {
            newsList = newsRepository.findAllWithCategories();
        }

        List<NewsHeadlineDto> dtos = newsList.stream()
                .map(NewsHeadlineDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Get complete news item by ID, including content, categories, and comments.
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<NewsDetailDto> getNewsDetail(@PathVariable Long id) {
        return newsRepository.findById(id)
                .map(NewsDetailDto::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get comments for a specific news item.
     */
    @GetMapping("/{id}/comments")
    @Transactional(readOnly = true)
    public ResponseEntity<List<CommentDto>> getNewsComments(@PathVariable Long id) {
        if (!newsRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<CommentDto> comments = commentRepository.findByNews_IdOrderByCreatedAtDesc(id)
                .stream()
                .map(CommentDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    /**
     * Add a comment to a news item. Requires reader JWT authentication.
     */
    @PostMapping("/{id}/comments")
    @Transactional
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        News news = newsRepository.findById(id)
                .orElse(null);

        if (news == null) {
            return ResponseEntity.notFound().build();
        }

        User user = null;
        String authorName = request.getAuthorName();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            user = userRepository.findById(userDetails.getId()).orElse(null);
            authorName = userDetails.getUsername();
        }

        if (authorName == null || authorName.trim().isEmpty()) {
            authorName = "Anonymous Reader";
        }

        Comment comment = new Comment(news, user, authorName, request.getCommentText());
        Comment savedComment = commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentDto.fromEntity(savedComment));
    }
}
