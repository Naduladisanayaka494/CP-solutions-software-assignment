package dao;

import models.Comment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {

    private final Connection connection;

    public CommentDao(Connection connection) {
        this.connection = connection;
    }

    public List<Comment> getCommentsByNewsId(int newsId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT id, news_id, author_name, comment_text, created_at " +
                     "FROM comments WHERE news_id = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newsId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Comment(
                            rs.getInt("id"),
                            rs.getInt("news_id"),
                            rs.getString("author_name"),
                            rs.getString("comment_text"),
                            rs.getString("created_at")
                    ));
                }
            }
        }
        return list;
    }

    public void addComment(int newsId, String authorName, String commentText) throws SQLException {
        String sql = "INSERT INTO comments (news_id, author_name, comment_text, created_at) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newsId);
            ps.setString(2, authorName != null && !authorName.trim().isEmpty() ? authorName.trim() : "Anonymous Reader");
            ps.setString(3, commentText);
            ps.executeUpdate();
        }
    }
}
