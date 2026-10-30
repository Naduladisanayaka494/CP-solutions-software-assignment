package models;

public class Comment {
    private int id;
    private int newsId;
    private String authorName;
    private String commentText;
    private String createdAt;

    public Comment() {}

    public Comment(int id, int newsId, String authorName, String commentText, String createdAt) {
        this.id = id;
        this.newsId = newsId;
        this.authorName = authorName;
        this.commentText = commentText;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNewsId() { return newsId; }
    public void setNewsId(int newsId) { this.newsId = newsId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
