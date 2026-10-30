package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CommentDao;
import dao.NewsDao;
import models.Comment;
import models.News;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

public class NewsDetailHandler implements HttpHandler {

    private final Connection connection;

    public NewsDetailHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Integer newsId = null;
        if (query != null && query.contains("id=")) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals("id")) {
                    try {
                        newsId = Integer.parseInt(pair[1]);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        if (newsId == null) {
            byte[] msg = "Missing news article ID".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(400, msg.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(msg); }
            return;
        }

        try {
            NewsDao newsDao = new NewsDao(connection);
            CommentDao commentDao = new CommentDao(connection);

            News news = newsDao.getNewsById(newsId);
            if (news == null) {
                byte[] notFound = "Article not found".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(404, notFound.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(notFound); }
                return;
            }

            List<Comment> comments = commentDao.getCommentsByNewsId(newsId);
            String html = HtmlRenderer.renderNewsDetailPage(news, comments);
            byte[] bytes = html.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            byte[] errBytes = ("500 Internal Server Error: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, errBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errBytes);
            }
        }
    }
}
