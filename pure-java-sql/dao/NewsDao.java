package dao;

import models.Category;
import models.News;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NewsDao {

    private final Connection connection;

    public NewsDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Fetch all news headlines ordered by published date descending.
     */
    public List<News> getAllNews() throws SQLException {
        List<News> list = new ArrayList<>();
        String sql = "SELECT id, title, summary, content, author, published_at FROM news ORDER BY published_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                News n = mapNewsRow(rs);
                n.setCategories(getCategoriesForNews(n.getId()));
                list.add(n);
            }
        }
        return list;
    }

    /**
     * Fetch news headlines that belong to a specific category.
     * Demonstrates Many-to-Many query via join table news_categories.
     */
    public List<News> getNewsByCategoryId(int categoryId) throws SQLException {
        List<News> list = new ArrayList<>();
        String sql = "SELECT DISTINCT n.id, n.title, n.summary, n.content, n.author, n.published_at " +
                     "FROM news n " +
                     "JOIN news_categories nc ON n.id = nc.news_id " +
                     "WHERE nc.category_id = ? " +
                     "ORDER BY n.published_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    News n = mapNewsRow(rs);
                    n.setCategories(getCategoriesForNews(n.getId()));
                    list.add(n);
                }
            }
        }
        return list;
    }

    /**
     * Fetch complete news article by ID.
     */
    public News getNewsById(int id) throws SQLException {
        String sql = "SELECT id, title, summary, content, author, published_at FROM news WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    News n = mapNewsRow(rs);
                    n.setCategories(getCategoriesForNews(n.getId()));
                    return n;
                }
            }
        }
        return null;
    }

    /**
     * Fetch all categories associated with a news item.
     * For example, "Sl lost" will return both Sports and Entertainment.
     */
    public List<Category> getCategoriesForNews(int newsId) throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.description " +
                     "FROM categories c " +
                     "JOIN news_categories nc ON c.id = nc.category_id " +
                     "WHERE nc.news_id = ? " +
                     "ORDER BY c.name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newsId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    ));
                }
            }
        }
        return list;
    }

    private News mapNewsRow(ResultSet rs) throws SQLException {
        return new News(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("summary"),
                rs.getString("content"),
                rs.getString("author"),
                rs.getString("published_at")
        );
    }
}
