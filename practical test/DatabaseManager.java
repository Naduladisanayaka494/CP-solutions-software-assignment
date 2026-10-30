import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPass;
    private static boolean isMySql = false;

    public static synchronized void init() {
        String mysqlUrl = "jdbc:mysql://localhost:3306/newsdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=1000&socketTimeout=1000";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(mysqlUrl, "root", "rootpassword");
            conn.close();
            dbUrl = mysqlUrl;
            dbUser = "root";
            dbPass = "rootpassword";
            isMySql = true;
            System.out.println("[DatabaseManager] Connected successfully to MySQL on localhost:3306.");
        } catch (Exception e) {
            // Fallback to embedded H2 database (in MySQL syntax mode)
            System.out.println("[DatabaseManager] MySQL not reachable on port 3306 (" + e.getMessage() + ").");
            System.out.println("[DatabaseManager] Falling back to embedded SQL database (H2 in MySQL mode).");
            dbUrl = "jdbc:h2:mem:newsdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE";
            dbUser = "sa";
            dbPass = "";
            isMySql = false;
        }

        initTables();
    }

    public static Connection getConnection() throws SQLException {
        if (dbUrl == null) {
            init();
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    private static void initTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS categories (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL UNIQUE, " +
                    "description VARCHAR(255))");

            stmt.execute("CREATE TABLE IF NOT EXISTS news (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "summary VARCHAR(500), " +
                    "content TEXT NOT NULL, " +
                    "author VARCHAR(100) DEFAULT 'Staff Reporter', " +
                    "published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");


            stmt.execute("CREATE TABLE IF NOT EXISTS news_categories (" +
                    "news_id INT NOT NULL, " +
                    "category_id INT NOT NULL, " +
                    "PRIMARY KEY (news_id, category_id), " +
                    "FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE)");


            stmt.execute("CREATE TABLE IF NOT EXISTS comments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "news_id INT NOT NULL, " +
                    "author_name VARCHAR(100) NOT NULL, " +
                    "comment_text TEXT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (news_id) REFERENCES news(id) ON DELETE CASCADE)");


            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories");
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("[DatabaseManager] Seeding initial SQL data...");


                stmt.execute("INSERT INTO categories (id, name, description) VALUES " +
                        "(1, 'Sports', 'Athletic events, matches, tournaments, and sports updates'), " +
                        "(2, 'Entertainment', 'Cinema, music, celebrity culture, and pop buzz'), " +
                        "(3, 'Technology', 'Computing, gadgets, artificial intelligence, and innovations'), " +
                        "(4, 'World News', 'Global affairs and international relations')");


                stmt.execute("INSERT INTO news (id, title, summary, content, author) VALUES " +
                        "(1, 'Sl lost', " +
                        "'Dramatic defeat sparks nationwide entertainment and sports commentary as final over slips away.', " +
                        "'The national cricket team suffered a nail-biting defeat in the final over of yesterday''s thriller. While sports analysts dissect bowling strategies, the dramatic post-match press conference and unexpected celebrity reactions have made it the top trending topic across sports talk shows and entertainment channels alike.', " +
                        "'Nuwan Perera'), " +
                        "(2, 'Championship Finals Scheduled for This Sunday', " +
                        "'Top regional teams gear up for high-stakes clash this coming weekend.', " +
                        "'The premier regional athletic championships reach their climax this Sunday at the National Stadium. Over 30,000 spectators are expected to attend what promises to be one of the most competitive finishes in recent history.', " +
                        "'Samantha Dias'), " +
                        "(3, 'Blockbuster Film Breaks Global Opening Weekend Box Office Records', " +
                        "'Record-smashing box office figures stunned industry insiders worldwide.', " +
                        "'The newest cinematic installment has surpassed all projections, grossing over $250 million across its debut weekend. Critics praise the groundbreaking visual effects and captivating ensemble performances.', " +
                        "'Arun Kumar'), " +
                        "(4, 'Next-Gen AI Systems Transform Modern Healthcare Diagnostics', " +
                        "'Hospitals adopt state-of-the-art neural networks to assist clinical specialists.', " +
                        "'Pioneering artificial intelligence algorithms are demonstrating unprecedented accuracy in early medical screenings, reducing patient diagnostic wait times and aiding healthcare professionals in complex cases.', " +
                        "'Elena Rostova')");


                stmt.execute("INSERT INTO news_categories (news_id, category_id) VALUES " +
                        "(1, 1), (1, 2), (2, 1), (3, 2), (4, 3)");


                stmt.execute("INSERT INTO comments (news_id, author_name, comment_text) VALUES " +
                        "(1, 'reader_kamal', 'Hard luck! The middle overs were good, but the death overs cost us the game.'), " +
                        "(1, 'sports_fan99', 'Entertaining game regardless, kept everyone on the edge of their seats!')");

                System.out.println("[DatabaseManager] Seed data inserted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
