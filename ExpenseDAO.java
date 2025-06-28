package com.example.expensemanager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    public void addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (title, expense_date, amount, unit) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, expense.getTitle());
            pstmt.setDate(2, expense.getExpenseSqlDate()); // Convert LocalDate to java.sql.Date
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setString(4, expense.getUnit());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        expense.setId(generatedKeys.getInt(1));
                    } else {
                        System.err.println("Creating expense failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Expense getExpenseById(int id) {
        String sql = "SELECT id, title, expense_date, amount, unit FROM expenses WHERE id = ?";
        Expense expense = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    LocalDate expenseDate = rs.getDate("expense_date").toLocalDate();
                    double amount = rs.getDouble("amount");
                    String unit = rs.getString("unit");
                    expense = new Expense(id, title, expenseDate, amount, unit);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting expense by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return expense;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, title, expense_date, amount, unit FROM expenses ORDER BY expense_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                LocalDate expenseDate = rs.getDate("expense_date").toLocalDate();
                double amount = rs.getDouble("amount");
                String unit = rs.getString("unit");
                expenses.add(new Expense(id, title, expenseDate, amount, unit));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all expenses: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }

    public boolean updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET title = ?, expense_date = ?, amount = ?, unit = ? WHERE id = ?";
        boolean rowUpdated = false;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, expense.getTitle());
            pstmt.setDate(2, expense.getExpenseSqlDate());
            pstmt.setDouble(3, expense.getAmount());
            pstmt.setString(4, expense.getUnit());
            pstmt.setInt(5, expense.getId());
            rowUpdated = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating expense: " + e.getMessage());
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public boolean deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        boolean rowDeleted = false;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            rowDeleted = pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting expense: " + e.getMessage());
            e.printStackTrace();
        }
        return rowDeleted;
    }
}
