import com.sun.net.httpserver.HttpServer;
import server.AddCommentHandler;
import server.HomeHandler;
import server.NewsDetailHandler;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.concurrent.Executors;

public class Main {

    public static final int PORT = 8081;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   Pure Java & SQL News Website Server");
        System.out.println("=================================================");

        try {

            DatabaseManager.init();
            Connection conn = DatabaseManager.getConnection();


            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

         
            server.createContext("/", new HomeHandler(conn));
            server.createContext("/news", new NewsDetailHandler(conn));
            server.createContext("/add-comment", new AddCommentHandler(conn));

            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();

            System.out.println("\n Server is running!");
            System.out.println(" Open your browser and navigate to:");
            System.out.println(" 👉 http://localhost:" + PORT);
            System.out.println("\nFeatures:");
            System.out.println(" - Category Selection & Headlines Filter (/ or /?categoryId=1)");
            System.out.println(" - Multi-category news ('Sl lost' belongs to both Sports & Entertainment)");
            System.out.println(" - Full Story Reading Page (/news?id=1)");
            System.out.println(" - Readers Comments & Add Comment Form (/add-comment)");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
