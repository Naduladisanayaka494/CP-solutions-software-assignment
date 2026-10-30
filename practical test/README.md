# Practical Test: Pure Java & SQL News Website

A complete, standalone news website built purely with **Java (Standard Library)** and **SQL**, with zero external frameworks (no Spring Boot, no React, no Node.js).

---

## Features

1. **Category Selection & Headlines Filter**:
   - The user can select a category from all available categories (*Sports*, *Entertainment*, *Technology*, *World News*, or *All*).
   - Shows the news headlines belonging to the selected category.
2. **Multi-Category Relationship (Many-to-Many)**:
   - A single news item can belong to multiple categories.
   - Example: **"Sl lost"** belongs to both **Sports** and **Entertainment** categories, and displays both badges.
3. **Full Story Navigation**:
   - Clicking any headline navigates the reader to the full article page (`/news?id=X`) displaying the complete content, author, date, and categories.
4. **Reader Comments**:
   - Readers can view all comments for an article and submit new comments via the interactive comment box (`POST /add-comment`), which persists directly to SQL.
5. **Universal Database Compatibility**:
   - Supports native **MySQL** via JDBC.
   - Includes automatic failover to an embedded in-memory SQL database if MySQL is not currently running on port 3306, allowing anyone to run and test it immediately.

---

## How to Compile and Run

### Prerequisites
- **Java JDK** (Java 8 or higher: JDK 8, 11, 17, or 21)

### Steps

1. Open your terminal / command prompt and navigate to this folder:
   ```powershell
   cd "c:\Java-answers\practical test"
   ```

2. Compile all Java files:
   ```powershell
   javac -cp ".;lib/*" Main.java DatabaseManager.java models/*.java dao/*.java server/*.java
   ```

3. Run the application:
   ```powershell
   java -cp ".;lib/*" Main
   ```

4. Open your browser and visit:
   👉 **`http://localhost:8081`**

---

## How to Run the Automated Test Suite

To run the automated verification suite that validates category filtering, Many-to-Many queries, and comment posting:

```powershell
cd "c:\Java-answers\practical test"
javac -cp ".;lib/*" DatabaseManager.java TestRunner.java
java -cp ".;lib/*" TestRunner
```

Expected output:
```text
>>> Running Automated Verification Suite...
  [PASS] 1. Home Page & Categories rendering
  [PASS] 2. Category 1 (Sports) contains 'Sl lost'
  [PASS] 3. Category 2 (Entertainment) ALSO contains 'Sl lost' (Many-to-Many)
  [PASS] 4. News Detail Page displays full story & comments
  [PASS] 5. Add Comment POST returned 302 Redirect
  [PASS] 6. Newly posted comment verified in database & rendered on page

🎉 ALL 6 TESTS PASSED SUCCESSFULLY!
```

---

## Database Schema (`schema.sql`)

The database schema and seed data are stored in [`schema.sql`](schema.sql):
- **`categories`**: `id`, `name`, `description`
- **`news`**: `id`, `title`, `summary`, `content`, `author`, `published_at`
- **`news_categories`**: `news_id`, `category_id` (Many-to-Many Join Table)
- **`comments`**: `id`, `news_id`, `author_name`, `comment_text`, `created_at`
