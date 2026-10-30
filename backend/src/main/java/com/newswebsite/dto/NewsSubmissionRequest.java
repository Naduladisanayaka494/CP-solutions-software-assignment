package com.newswebsite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class NewsSubmissionRequest {

    @NotBlank(message = "Title / Headline is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 500, message = "Summary cannot exceed 500 characters")
    private String summary;

    @NotBlank(message = "Article content is required")
    private String content;

    @NotEmpty(message = "At least one category must be selected")
    private List<Long> categoryIds;

    public NewsSubmissionRequest() {}

    public NewsSubmissionRequest(String title, String summary, String content, List<Long> categoryIds) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.categoryIds = categoryIds;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<Long> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Long> categoryIds) { this.categoryIds = categoryIds; }
}
