package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CategoryDao;
import dao.NewsDao;
import models.Category;
import models.News;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

public class HomeHandler implements HttpHandler {

    private final Connection connection;

    public HomeHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (!path.equals("/")) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        try {
            CategoryDao categoryDao = new CategoryDao(connection);
            NewsDao newsDao = new NewsDao(connection);

            List<Category> categories = categoryDao.getAllCategories();

            // Check for categoryId in query string: /?categoryId=X
            String query = exchange.getRequestURI().getQuery();
            Integer selectedCatId = null;
            if (query != null && query.contains("categoryId=")) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && pair[0].equals("categoryId")) {
                        try {
                            selectedCatId = Integer.parseInt(pair[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            List<News> newsList;
            if (selectedCatId != null) {
                newsList = newsDao.getNewsByCategoryId(selectedCatId);
            } else {
                newsList = newsDao.getAllNews();
            }

            String html = HtmlRenderer.renderHeadlinesPage(categories, selectedCatId, newsList);
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
