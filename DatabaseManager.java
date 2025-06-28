package com.example.expensemanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // IMPORTANT: Replace with your actual MySQL username and password
    // For a real application, use a configuration file or environment variables.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expenses_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASS = "root"; // Replace with your MySQL password

    // Load the JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Include it in your library path.");
            e.printStackTrace();
            // Consider throwing a runtime exception or exiting if driver is critical
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS expenses (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "title VARCHAR(255) NOT NULL," +
                     "expense_date DATE NOT NULL," +
                     "amount DECIMAL(10, 2) NOT NULL," +
                     "unit VARCHAR(50)" +
                     ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table 'expenses' checked/created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // A simple test to ensure connection and table creation works.
        // You would typically call createTableIfNotExists() once at application startup.
        createTableIfNotExists();
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Successfully connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
