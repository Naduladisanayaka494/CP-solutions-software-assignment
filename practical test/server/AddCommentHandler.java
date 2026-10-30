package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CommentDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class AddCommentHandler implements HttpHandler {

    private final Connection connection;

    public AddCommentHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            Map<String, String> form = parseFormData(body.toString());
            int newsId = Integer.parseInt(form.getOrDefault("newsId", "0"));
            String authorName = form.getOrDefault("authorName", "Anonymous Reader");
            String commentText = form.getOrDefault("commentText", "");

            if (newsId > 0 && !commentText.trim().isEmpty()) {
                CommentDao commentDao = new CommentDao(connection);
                commentDao.addComment(newsId, authorName, commentText);
            }

            // Redirect back to the news detail page
            exchange.getResponseHeaders().set("Location", "/news?id=" + newsId);
            exchange.sendResponseHeaders(302, -1);
        } catch (Exception e) {
            e.printStackTrace();
            byte[] err = ("Failed to post comment: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, err.length);
            exchange.getResponseBody().write(err);
        }
    }

    private Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        if (formData == null || formData.isEmpty()) return map;

        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length >= 1) {
                try {
                    String key = URLDecoder.decode(kv[0], "UTF-8");
                    String val = kv.length > 1 ? URLDecoder.decode(kv[1], "UTF-8") : "";
                    map.put(key, val);
                } catch (Exception ignored) {}
            }
        }
        return map;
    }
}
