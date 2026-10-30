package com.newswebsite.dto;

import com.newswebsite.model.News;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NewsDetailDto {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String author;
    private LocalDateTime publishedAt;
    private List<CategoryDto> categories;
    private List<CommentDto> comments;

    public NewsDetailDto() {}

    public NewsDetailDto(Long id, String title, String summary, String content, String author,
                         LocalDateTime publishedAt, List<CategoryDto> categories, List<CommentDto> comments) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.author = author;
        this.publishedAt = publishedAt;
        this.categories = categories;
        this.comments = comments;
    }

    public static NewsDetailDto fromEntity(News news) {
        if (news == null) return null;
        List<CategoryDto> catDtos = news.getCategories() != null
            ? news.getCategories().stream().map(CategoryDto::fromEntity).collect(Collectors.toList())
            : List.of();

        List<CommentDto> commentDtos = news.getComments() != null
            ? news.getComments().stream().map(CommentDto::fromEntity).collect(Collectors.toList())
            : List.of();

        return new NewsDetailDto(
            news.getId(),
            news.getTitle(),
            news.getSummary(),
            news.getContent(),
            news.getAuthor(),
            news.getPublishedAt(),
            catDtos,
            commentDtos
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public List<CategoryDto> getCategories() { return categories; }
    public void setCategories(List<CategoryDto> categories) { this.categories = categories; }

    public List<CommentDto> getComments() { return comments; }
    public void setComments(List<CommentDto> comments) { this.comments = comments; }
}
