package com.newswebsite.dto;

import com.newswebsite.model.News;
import com.newswebsite.model.NewsStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NewsHeadlineDto {
    private Long id;
    private String title;
    private String summary;
    private String author;
    private Long authorId;
    private NewsStatus status;
    private LocalDateTime publishedAt;
    private List<CategoryDto> categories;

    public NewsHeadlineDto() {}

    public NewsHeadlineDto(Long id, String title, String summary, String author, Long authorId,
                           NewsStatus status, LocalDateTime publishedAt, List<CategoryDto> categories) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.author = author;
        this.authorId = authorId;
        this.status = status;
        this.publishedAt = publishedAt;
        this.categories = categories;
    }

    public static NewsHeadlineDto fromEntity(News news) {
        if (news == null) return null;
        List<CategoryDto> catDtos = news.getCategories() != null
            ? news.getCategories().stream().map(CategoryDto::fromEntity).collect(Collectors.toList())
            : List.of();

        Long aId = news.getAuthorUser() != null ? news.getAuthorUser().getId() : null;

        return new NewsHeadlineDto(
            news.getId(),
            news.getTitle(),
            news.getSummary(),
            news.getAuthor(),
            aId,
            news.getStatus(),
            news.getPublishedAt(),
            catDtos
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public NewsStatus getStatus() { return status; }
    public void setStatus(NewsStatus status) { this.status = status; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public List<CategoryDto> getCategories() { return categories; }
    public void setCategories(List<CategoryDto> categories) { this.categories = categories; }
}
