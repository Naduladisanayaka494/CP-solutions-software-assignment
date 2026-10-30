package com.newswebsite.dto;

import com.newswebsite.model.Comment;
import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private Long newsId;
    private Long userId;
    private String authorName;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentDto() {}

    public CommentDto(Long id, Long newsId, Long userId, String authorName, String commentText, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.newsId = newsId;
        this.userId = userId;
        this.authorName = authorName;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentDto fromEntity(Comment comment) {
        if (comment == null) return null;
        Long nId = comment.getNews() != null ? comment.getNews().getId() : null;
        Long uId = comment.getUser() != null ? comment.getUser().getId() : null;
        return new CommentDto(
            comment.getId(),
            nId,
            uId,
            comment.getAuthorName(),
            comment.getCommentText(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
