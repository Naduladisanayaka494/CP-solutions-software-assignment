# CP Solutions & Software Assignment

Java solutions for algorithmic problems and practical full-stack/SQL news website tests.

---

## 1. Programming Problems

- **`Problem1.java`**: Sum of a list using for-loop, while-loop, and recursion.
- **`Problem2.java`**: Combines two lists by alternatingly taking elements.
- **`Problem3.java`**: Computes the first 100 Fibonacci numbers.
- **`Problem4.java`**: Arranges a list of integers to form the largest possible number.

---

## 2. Practical Test (News Website in Pure Java & SQL)

Located in **[`practical test/`](practical%20test/)**:

A complete news website built purely with **Java (Standard Library `HttpServer`)** and **SQL (raw JDBC)** without external frameworks.

### How to Run:
```powershell
cd "practical test"
javac -cp ".;lib/*" Main.java DatabaseManager.java models/*.java dao/*.java server/*.java
java -cp ".;lib/*" Main
```
Navigate to: **`http://localhost:8081`**

### How to Test:
```powershell
cd "practical test"
javac -cp ".;lib/*" DatabaseManager.java TestRunner.java
java -cp ".;lib/*" TestRunner
```
