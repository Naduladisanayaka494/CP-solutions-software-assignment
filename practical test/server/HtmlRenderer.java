package server;

import models.Category;
import models.Comment;
import models.News;

import java.util.List;

public class HtmlRenderer {

    private static final String CSS = 
        ":root { --primary: #1e3a8a; --accent: #dc2626; --bg: #f8fafc; --card: #ffffff; --text: #0f172a; --muted: #64748b; --border: #e2e8f0; }" +
        "* { box-sizing: border-box; margin: 0; padding: 0; }" +
        "body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background: var(--bg); color: var(--text); line-height: 1.6; }" +
        ".container { max-width: 1000px; margin: 0 auto; padding: 0 1.25rem; }" +
        "header { background: #fff; border-bottom: 1px solid var(--border); padding: 1.25rem 0; box-shadow: 0 1px 2px rgba(0,0,0,0.05); }" +
        ".logo { font-size: 1.6rem; font-weight: 800; text-decoration: none; color: var(--primary); display: inline-flex; align-items: center; gap: 0.5rem; }" +
        ".logo span { color: #2563eb; }" +
        ".cat-nav { background: #fff; border-bottom: 1px solid var(--border); padding: 0.85rem 0; }" +
        ".cat-list { display: flex; gap: 0.6rem; overflow-x: auto; list-style: none; }" +
        ".cat-pill { text-decoration: none; padding: 0.45rem 1rem; border-radius: 9999px; font-size: 0.875rem; font-weight: 600; background: #f1f5f9; color: var(--muted); transition: all 0.2s; }" +
        ".cat-pill:hover, .cat-pill.active { background: var(--primary); color: #fff; }" +
        ".section-title { font-size: 1.5rem; font-weight: 800; margin: 2rem 0 1.25rem 0; }" +
        ".grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.5rem; margin-bottom: 3rem; }" +
        ".card { background: var(--card); border: 1px solid var(--border); border-radius: 0.6rem; padding: 1.35rem; display: flex; flex-direction: column; justify-content: space-between; transition: transform 0.2s, box-shadow 0.2s; }" +
        ".card:hover { transform: translateY(-3px); box-shadow: 0 8px 16px rgba(0,0,0,0.06); border-color: #93c5fd; }" +
        ".card a.headline-link { text-decoration: none; color: var(--text); font-size: 1.2rem; font-weight: 700; margin-bottom: 0.5rem; display: block; }" +
        ".card a.headline-link:hover { color: var(--primary); }" +
        ".card p { font-size: 0.9rem; color: var(--muted); margin-bottom: 1rem; flex-grow: 1; }" +
        ".card-meta { display: flex; justify-content: space-between; font-size: 0.775rem; color: #94a3b8; border-top: 1px solid #f1f5f9; padding-top: 0.75rem; }" +
        ".badges { display: flex; flex-wrap: wrap; gap: 0.35rem; margin-bottom: 0.75rem; }" +
        ".badge { display: inline-block; font-size: 0.7rem; font-weight: 700; text-transform: uppercase; padding: 0.2rem 0.5rem; border-radius: 0.25rem; background: #e0e7ff; color: #3730a3; }" +
        ".badge-sports { background: #fef3c7; color: #92400e; }" +
        ".badge-entertainment { background: #fce7f3; color: #9d174d; }" +
        ".badge-tech { background: #e0e7ff; color: #3730a3; }" +
        ".btn { display: inline-block; background: var(--primary); color: #fff; padding: 0.6rem 1.2rem; border-radius: 0.4rem; font-weight: 600; text-decoration: none; border: none; cursor: pointer; }" +
        ".btn:hover { background: #172554; }" +
        ".back-link { display: inline-block; margin: 1.5rem 0 1rem 0; color: var(--muted); text-decoration: none; font-weight: 600; font-size: 0.9rem; }" +
        ".back-link:hover { color: var(--primary); }" +
        ".article-box { background: var(--card); border: 1px solid var(--border); border-radius: 0.6rem; padding: 2.25rem; margin-bottom: 2rem; }" +
        ".article-title { font-size: 2.2rem; font-weight: 800; margin: 0.5rem 0 1rem 0; line-height: 1.25; }" +
        ".article-meta { color: var(--muted); font-size: 0.85rem; margin-bottom: 1.75rem; border-bottom: 1px solid var(--border); padding-bottom: 1rem; }" +
        ".article-body { font-size: 1.15rem; line-height: 1.8; color: #334155; margin-bottom: 2.5rem; }" +
        ".comments-title { font-size: 1.35rem; font-weight: 700; margin-bottom: 1.25rem; border-top: 2px solid var(--border); padding-top: 2rem; }" +
        ".comment-card { background: #f8fafc; border: 1px solid var(--border); border-radius: 0.4rem; padding: 1rem; margin-bottom: 1rem; }" +
        ".comment-author { font-weight: 700; font-size: 0.9rem; color: var(--primary); }" +
        ".comment-date { font-size: 0.75rem; color: #94a3b8; margin-left: 0.5rem; }" +
        ".comment-text { font-size: 0.95rem; margin-top: 0.35rem; color: #334155; }" +
        ".form-box { background: #fff; border: 1px solid var(--border); border-radius: 0.5rem; padding: 1.5rem; margin-bottom: 2rem; }" +
        ".form-group { margin-bottom: 1rem; }" +
        ".form-group label { display: block; font-weight: 600; font-size: 0.875rem; margin-bottom: 0.35rem; }" +
        ".form-control { width: 100%; padding: 0.6rem 0.8rem; border: 1px solid var(--border); border-radius: 0.35rem; font-size: 0.95rem; outline: none; }" +
        ".form-control:focus { border-color: var(--primary); }";

    public static String renderHeadlinesPage(List<Category> categories, Integer selectedCatId, List<News> newsList) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        sb.append("<title>GlobalNews Portal (Pure Java + SQL)</title>");
        sb.append("<style>").append(CSS).append("</style></head><body>");

        // Header
        sb.append("<header><div class='container'>");
        sb.append("<a href='/' class='logo'>📰 Global<span>News</span> <small style='font-size:0.75rem;background:#dbeafe;padding:0.2rem 0.5rem;border-radius:0.3rem'>Pure Java & SQL</small></a>");
        sb.append("</div></header>");

        // Category Navigation Bar
        sb.append("<nav class='cat-nav'><div class='container'><ul class='cat-list'>");
        sb.append("<li><a href='/' class='cat-pill ").append(selectedCatId == null ? "active" : "").append("'>All Categories</a></li>");
        for (Category cat : categories) {
            boolean isAct = selectedCatId != null && selectedCatId == cat.getId();
            sb.append("<li><a href='/?categoryId=").append(cat.getId()).append("' class='cat-pill ").append(isAct ? "active" : "").append("'>")
              .append(escapeHtml(cat.getName())).append("</a></li>");
        }
        sb.append("</ul></div></nav>");

        // Main Content
        sb.append("<main class='container'>");
        String catName = "All News";
        if (selectedCatId != null) {
            for (Category c : categories) {
                if (c.getId() == selectedCatId) {
                    catName = c.getName();
                    break;
                }
            }
        }

        sb.append("<h1 class='section-title'>").append(escapeHtml(catName)).append(" Headlines (").append(newsList.size()).append(")</h1>");

        if (newsList.isEmpty()) {
            sb.append("<p style='padding: 2rem 0; color: var(--muted);'>No news items found in this category.</p>");
        } else {
            sb.append("<div class='grid'>");
            for (News n : newsList) {
                sb.append("<article class='card'>");
                sb.append("<div>");

                // Badges
                sb.append("<div class='badges'>");
                for (Category c : n.getCategories()) {
                    String bClass = "badge";
                    if (c.getName().toLowerCase().contains("sport")) bClass += " badge-sports";
                    else if (c.getName().toLowerCase().contains("entertain")) bClass += " badge-entertainment";
                    else if (c.getName().toLowerCase().contains("tech")) bClass += " badge-tech";
                    sb.append("<span class='").append(bClass).append("'>").append(escapeHtml(c.getName())).append("</span>");
                }
                sb.append("</div>");

                // Clickable headline navigating to article detail page
                sb.append("<a href='/news?id=").append(n.getId()).append("' class='headline-link'>").append(escapeHtml(n.getTitle())).append("</a>");
                sb.append("<p>").append(escapeHtml(n.getSummary())).append("</p>");
                sb.append("</div>");

                sb.append("<div class='card-meta'>");
                sb.append("<span>By ").append(escapeHtml(n.getAuthor())).append("</span>");
                sb.append("<span>").append(escapeHtml(n.getPublishedAt())).append("</span>");
                sb.append("</div>");
                sb.append("</article>");
            }
            sb.append("</div>");
        }

        sb.append("</main></body></html>");
        return sb.toString();
    }

    public static String renderNewsDetailPage(News news, List<Comment> comments) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        sb.append("<title>").append(escapeHtml(news.getTitle())).append(" - GlobalNews</title>");
        sb.append("<style>").append(CSS).append("</style></head><body>");

        // Header
        sb.append("<header><div class='container'>");
        sb.append("<a href='/' class='logo'>📰 Global<span>News</span></a>");
        sb.append("</div></header>");

        sb.append("<main class='container'>");
        sb.append("<a href='/' class='back-link'>&larr; Back to all headlines</a>");

        sb.append("<article class='article-box'>");

        // Badges
        sb.append("<div class='badges'>");
        for (Category c : news.getCategories()) {
            String bClass = "badge";
            if (c.getName().toLowerCase().contains("sport")) bClass += " badge-sports";
            else if (c.getName().toLowerCase().contains("entertain")) bClass += " badge-entertainment";
            else if (c.getName().toLowerCase().contains("tech")) bClass += " badge-tech";
            sb.append("<span class='").append(bClass).append("'>").append(escapeHtml(c.getName())).append("</span>");
        }
        sb.append("</div>");

        sb.append("<h1 class='article-title'>").append(escapeHtml(news.getTitle())).append("</h1>");
        sb.append("<div class='article-meta'>By <strong>").append(escapeHtml(news.getAuthor())).append("</strong> &bull; Published: ").append(escapeHtml(news.getPublishedAt())).append("</div>");

        sb.append("<div class='article-body'>");
        String[] paras = news.getContent().split("\n\n");
        for (String p : paras) {
            sb.append("<p style='margin-bottom: 1.25rem;'>").append(escapeHtml(p)).append("</p>");
        }
        sb.append("</div>");

        // Reader Comments Section
        sb.append("<h2 class='comments-title'>💬 Reader Comments (").append(comments.size()).append(")</h2>");

        // Add Comment Form
        sb.append("<div class='form-box'>");
        sb.append("<h3 style='font-size:1.1rem; margin-bottom:1rem;'>Add a Comment</h3>");
        sb.append("<form method='POST' action='/add-comment'>");
        sb.append("<input type='hidden' name='newsId' value='").append(news.getId()).append("'>");
        sb.append("<div class='form-group'>");
        sb.append("<label>Your Name / Reader Handle</label>");
        sb.append("<input type='text' name='authorName' class='form-control' placeholder='e.g. reader_sam' required>");
        sb.append("</div>");
        sb.append("<div class='form-group'>");
        sb.append("<label>Comment</label>");
        sb.append("<textarea name='commentText' class='form-control' rows='3' placeholder='Share your thoughts...' required></textarea>");
        sb.append("</div>");
        sb.append("<button type='submit' class='btn'>Post Comment</button>");
        sb.append("</form>");
        sb.append("</div>");

        // Comments List
        if (comments.isEmpty()) {
            sb.append("<p style='color: var(--muted); font-style: italic;'>No comments yet. Be the first reader to comment!</p>");
        } else {
            for (Comment c : comments) {
                sb.append("<div class='comment-card'>");
                sb.append("<div><span class='comment-author'>").append(escapeHtml(c.getAuthorName())).append("</span>");
                sb.append("<span class='comment-date'>").append(escapeHtml(c.getCreatedAt())).append("</span></div>");
                sb.append("<div class='comment-text'>").append(escapeHtml(c.getCommentText())).append("</div>");
                sb.append("</div>");
            }
        }

        sb.append("</article>");
        sb.append("</main></body></html>");
        return sb.toString();
    }

    public static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
