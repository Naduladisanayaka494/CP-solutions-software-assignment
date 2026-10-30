package com.newswebsite.dto;

import com.newswebsite.model.Comment;
import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private Long newsId;
    private String authorName;
    private String commentText;
    private LocalDateTime createdAt;

    public CommentDto() {}

    public CommentDto(Long id, Long newsId, String authorName, String commentText, LocalDateTime createdAt) {
        this.id = id;
        this.newsId = newsId;
        this.authorName = authorName;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    public static CommentDto fromEntity(Comment comment) {
        if (comment == null) return null;
        Long nId = comment.getNews() != null ? comment.getNews().getId() : null;
        return new CommentDto(
            comment.getId(),
            nId,
            comment.getAuthorName(),
            comment.getCommentText(),
            comment.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
