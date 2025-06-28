package com.example.expensemanager;

import java.time.LocalDate;
import java.util.List;

public class ExpenseService {
    private ExpenseDAO expenseDAO;

    public ExpenseService() {
        this.expenseDAO = new ExpenseDAO();
    }

    public void addExpense(String title, LocalDate date, double amount, String unit) {
        if (title == null || title.trim().isEmpty()) {
            System.err.println("Expense title cannot be empty.");
            return;
        }
        if (date == null) {
            System.err.println("Expense date cannot be null.");
            return;
        }
        if (date.isAfter(LocalDate.now())) {
            System.err.println("Expense date cannot be in the future.");
            return;
        }
        if (amount <= 0) {
            System.err.println("Expense amount must be positive.");
            return;
        }
        if (unit == null || unit.trim().isEmpty()) {
            // Defaulting unit or could make it mandatory
            System.err.println("Expense unit cannot be empty.");
            return;
        }

        Expense expense = new Expense(title, date, amount, unit);
        expenseDAO.addExpense(expense);
        if (expense.getId() > 0) {
             System.out.println("Expense added successfully with ID: " + expense.getId());
        } else {
             System.err.println("Failed to add expense (DAO did not return a valid ID).");
        }
    }

    public Expense getExpenseById(int id) {
        if (id <= 0) {
            System.err.println("Invalid ID for fetching expense.");
            return null;
        }
        return expenseDAO.getExpenseById(id);
    }

    public List<Expense> getAllExpenses() {
        return expenseDAO.getAllExpenses();
    }

    public boolean updateExpense(int id, String newTitle, LocalDate newDate, double newAmount, String newUnit) {
        Expense existingExpense = expenseDAO.getExpenseById(id);
        if (existingExpense == null) {
            System.err.println("Expense with ID " + id + " not found. Cannot update.");
            return false;
        }

        // Apply updates only if new values are valid and different or provided
        boolean changed = false;
        if (newTitle != null && !newTitle.trim().isEmpty() && !newTitle.equals(existingExpense.getTitle())) {
            existingExpense.setTitle(newTitle);
            changed = true;
        }
        if (newDate != null && !newDate.isAfter(LocalDate.now()) && !newDate.equals(existingExpense.getExpenseDate())) {
            existingExpense.setExpenseDate(newDate);
            changed = true;
        }
        if (newAmount > 0 && newAmount != existingExpense.getAmount()) {
            existingExpense.setAmount(newAmount);
            changed = true;
        }
        if (newUnit != null && !newUnit.trim().isEmpty() && !newUnit.equals(existingExpense.getUnit())) {
            existingExpense.setUnit(newUnit);
            changed = true;
        }

        if (!changed) {
            System.out.println("No changes detected for expense ID " + id + ". Update not performed.");
            return false; // Or true, depending on desired behavior for no-op updates
        }

        boolean success = expenseDAO.updateExpense(existingExpense);
        if (success) {
            System.out.println("Expense ID " + id + " updated successfully.");
        } else {
            System.err.println("Failed to update expense ID " + id + ".");
        }
        return success;
    }

    public boolean deleteExpense(int id) {
        if (id <= 0) {
            System.err.println("Invalid ID for deleting expense.");
            return false;
        }
        Expense existingExpense = expenseDAO.getExpenseById(id);
        if (existingExpense == null) {
            System.err.println("Expense with ID " + id + " not found. Cannot delete.");
            return false;
        }
        boolean success = expenseDAO.deleteExpense(id);
        if (success) {
            System.out.println("Expense ID " + id + " deleted successfully.");
        } else {
            System.err.println("Failed to delete expense ID " + id + ".");
        }
        return success;
    }
}
