package com.newswebsite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {

    @NotBlank(message = "Comment text is required")
    @Size(min = 2, max = 2000, message = "Comment must be between 2 and 2000 characters")
    private String commentText;

    private String authorName;

    public CreateCommentRequest() {}

    public CreateCommentRequest(String commentText, String authorName) {
        this.commentText = commentText;
        this.authorName = authorName;
    }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
}
