package com.newswebsite.controller;

import com.newswebsite.dto.NewsDetailDto;
import com.newswebsite.dto.NewsHeadlineDto;
import com.newswebsite.dto.NewsSubmissionRequest;
import com.newswebsite.model.Category;
import com.newswebsite.model.News;
import com.newswebsite.model.NewsStatus;
import com.newswebsite.repository.CategoryRepository;
import com.newswebsite.repository.NewsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/news")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminNewsController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Get all articles pending approval from writers.
     */
    @GetMapping("/pending")
    @Transactional(readOnly = true)
    public ResponseEntity<List<NewsHeadlineDto>> getPendingArticles() {
        List<News> pendingList = newsRepository.findByStatusOrderByPublishedAtDesc(NewsStatus.PENDING_APPROVAL);
        List<NewsHeadlineDto> dtos = pendingList.stream()
                .map(NewsHeadlineDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Main Editor approves a writer's article, publishing it to the site.
     */
    @PutMapping("/{id}/approve")
    @Transactional
    public ResponseEntity<?> approveArticle(@PathVariable Long id) {
        News news = newsRepository.findById(id).orElse(null);
        if (news == null) {
            return ResponseEntity.notFound().build();
        }

        news.setStatus(NewsStatus.PUBLISHED);
        news.setPublishedAt(LocalDateTime.now());
        News saved = newsRepository.save(news);

        return ResponseEntity.ok(NewsDetailDto.fromEntity(saved));
    }

    /**
     * Main Editor rejects a writer's article.
     */
    @PutMapping("/{id}/reject")
    @Transactional
    public ResponseEntity<?> rejectArticle(@PathVariable Long id) {
        News news = newsRepository.findById(id).orElse(null);
        if (news == null) {
            return ResponseEntity.notFound().build();
        }

        news.setStatus(NewsStatus.REJECTED);
        News saved = newsRepository.save(news);

        return ResponseEntity.ok(NewsDetailDto.fromEntity(saved));
    }

    /**
     * Main Editor edits any news article.
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @Valid @RequestBody NewsSubmissionRequest request) {
        News news = newsRepository.findById(id).orElse(null);
        if (news == null) {
            return ResponseEntity.notFound().build();
        }

        news.setTitle(request.getTitle());
        news.setSummary(request.getSummary());
        news.setContent(request.getContent());

        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            news.setCategories(new HashSet<>(categories));
        }

        News saved = newsRepository.save(news);
        return ResponseEntity.ok(NewsDetailDto.fromEntity(saved));
    }

    /**
     * Main Editor deletes any news article.
     */
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        return newsRepository.findById(id).map(news -> {
            newsRepository.delete(news);
            return ResponseEntity.ok(Map.of("message", "Article deleted successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }
}
