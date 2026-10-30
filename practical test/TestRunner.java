import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestRunner {

    public static void main(String[] args) throws Exception {
        System.out.println(">>> Starting Pure Java News Server for Testing...");
        Thread serverThread = new Thread(() -> {
            Main.main(new String[]{});
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Wait for server to be responsive
        for (int i = 0; i < 20; i++) {
            try {
                HttpURLConnection testConn = (HttpURLConnection) new URL("http://localhost:8081/").openConnection();
                testConn.setConnectTimeout(500);
                if (testConn.getResponseCode() == 200) {
                    break;
                }
            } catch (Exception e) {
                Thread.sleep(500);
            }
        }

        System.out.println("\n>>> Running Automated Verification Suite...");

        // 1. Test Home page and category list
        String homeHtml = httpGet("http://localhost:8081/");
        assertTrue(homeHtml.contains("Sports") && homeHtml.contains("Entertainment"), "Home page should contain categories");
        assertTrue(homeHtml.contains("Sl lost"), "Home page should display headlines");
        System.out.println("  [PASS] 1. Home Page & Categories rendering");

        // 2. Test Category 1 (Sports) filtering - "Sl lost" must appear
        String sportsHtml = httpGet("http://localhost:8081/?categoryId=1");
        assertTrue(sportsHtml.contains("Sl lost"), "Sports category should contain 'Sl lost'");
        System.out.println("  [PASS] 2. Category 1 (Sports) contains 'Sl lost'");

        // 3. Test Category 2 (Entertainment) filtering - "Sl lost" must ALSO appear (Many-to-Many test)
        String entHtml = httpGet("http://localhost:8081/?categoryId=2");
        assertTrue(entHtml.contains("Sl lost"), "Entertainment category should also contain 'Sl lost'");
        System.out.println("  [PASS] 3. Category 2 (Entertainment) ALSO contains 'Sl lost' (Many-to-Many)");

        // 4. Test News Detail Page (/news?id=1)
        String detailHtml = httpGet("http://localhost:8081/news?id=1");
        assertTrue(detailHtml.contains("nail-biting defeat"), "Detail page should show complete article body");
        assertTrue(detailHtml.contains("reader_kamal"), "Detail page should show initial reader comment");
        System.out.println("  [PASS] 4. News Detail Page displays full story & comments");

        // 5. Test Add Comment POST
        String postBody = "newsId=1&authorName=pure_java_reader&commentText=Fantastic+article+tested+with+pure+Java";
        int statusCode = httpPost("http://localhost:8081/add-comment", postBody);
        assertTrue(statusCode == 302, "Add comment should redirect with 302 Found");
        System.out.println("  [PASS] 5. Add Comment POST returned 302 Redirect");

        // 6. Verify newly posted comment is persisted and visible on detail page
        String updatedDetail = httpGet("http://localhost:8081/news?id=1");
        assertTrue(updatedDetail.contains("pure_java_reader"), "Detail page should now show the new comment author");
        assertTrue(updatedDetail.contains("Fantastic article tested with pure Java"), "Detail page should now show the new comment text");
        System.out.println("  [PASS] 6. Newly posted comment verified in database & rendered on page");

        System.out.println("\n🎉 ALL 6 TESTS PASSED SUCCESSFULLY!");
        System.exit(0);
    }

    private static String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        }
    }

    private static int httpPost(String urlStr, String body) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
        }
        return conn.getResponseCode();
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.err.println("TEST FAILURE: " + message);
            System.exit(1);
        }
    }
}
