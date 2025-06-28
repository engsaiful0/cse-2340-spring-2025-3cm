package com.example.expensemanager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainApp {
    private static ExpenseService expenseService = new ExpenseService();
    private static Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        // Ensure the database table is ready
        DatabaseManager.createTableIfNotExists();

        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addExpenseUI();
                        break;
                    case 2:
                        viewAllExpensesUI();
                        break;
                    case 3:
                        updateExpenseUI();
                        break;
                    case 4:
                        deleteExpenseUI();
                        break;
                    case 5:
                        viewExpenseByIdUI();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Exiting application. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number for menu choice.");
                scanner.nextLine(); // Consume the invalid input
            }
            System.out.println("------------------------------------");
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- Expense Manager Menu ---");
        System.out.println("1. Add Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. Update Expense");
        System.out.println("4. Delete Expense");
        System.out.println("5. View Expense by ID");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addExpenseUI() {
        System.out.println("\n--- Add New Expense ---");
        System.out.print("Enter title: ");
        String title = scanner.nextLine();

        LocalDate date = null;
        while (date == null) {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine();
            try {
                date = LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        double amount = 0;
        boolean validAmount = false;
        while(!validAmount) {
            System.out.print("Enter amount: ");
            try {
                amount = scanner.nextDouble();
                if (amount <= 0) {
                    System.err.println("Amount must be positive.");
                } else {
                    validAmount = true;
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input for amount. Please enter a number.");
            } finally {
                 scanner.nextLine(); // Consume newline left-over
            }
        }

        System.out.print("Enter unit (e.g., USD, INR, items): ");
        String unit = scanner.nextLine();

        expenseService.addExpense(title, date, amount, unit);
    }

    private static void viewAllExpensesUI() {
        System.out.println("\n--- All Expenses ---");
        List<Expense> expenses = expenseService.getAllExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
        } else {
            System.out.printf("%-5s | %-20s | %-12s | %-10s | %-10s\n", "ID", "Title", "Date", "Amount", "Unit");
            System.out.println("--------------------------------------------------------------------");
            for (Expense expense : expenses) {
                System.out.printf("%-5d | %-20s | %-12s | %-10.2f | %-10s\n",
                        expense.getId(),
                        expense.getTitle(),
                        expense.getExpenseDate().format(DATE_FORMATTER),
                        expense.getAmount(),
                        expense.getUnit());
            }
        }
    }

    private static void updateExpenseUI() {
        System.out.println("\n--- Update Expense ---");
        viewAllExpensesUI(); // Show expenses to help user choose
        System.out.print("Enter ID of the expense to update: ");
        int id = -1;
        try {
            id = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.err.println("Invalid ID format.");
            scanner.nextLine(); // consume invalid input
            return;
        }

        Expense existingExpense = expenseService.getExpenseById(id);
        if (existingExpense == null) {
            System.err.println("Expense with ID " + id + " not found.");
            return;
        }

        System.out.println("Updating expense: " + existingExpense.getTitle());
        System.out.print("Enter new title (or press Enter to keep '" + existingExpense.getTitle() + "'): ");
        String newTitle = scanner.nextLine();
        if (newTitle.trim().isEmpty()) newTitle = existingExpense.getTitle();


        LocalDate newDate = null;
        System.out.print("Enter new date (YYYY-MM-DD, or press Enter to keep '" + existingExpense.getExpenseDate().format(DATE_FORMATTER) + "'): ");
        String newDateStr = scanner.nextLine();
        if (newDateStr.trim().isEmpty()) {
            newDate = existingExpense.getExpenseDate();
        } else {
            try {
                newDate = LocalDate.parse(newDateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format. Keeping original date: " + existingExpense.getExpenseDate().format(DATE_FORMATTER));
                newDate = existingExpense.getExpenseDate(); // Keep original if parse fails
            }
        }

        double newAmount = 0;
        System.out.print("Enter new amount (or press Enter to keep '" + existingExpense.getAmount() + "'): ");
        String newAmountStr = scanner.nextLine();
        if (newAmountStr.trim().isEmpty()) {
            newAmount = existingExpense.getAmount();
        } else {
            try {
                newAmount = Double.parseDouble(newAmountStr);
                if (newAmount <= 0) {
                     System.err.println("Amount must be positive. Keeping original amount: " + existingExpense.getAmount());
                     newAmount = existingExpense.getAmount();
                }
            } catch (NumberFormatException e) {
                 System.err.println("Invalid amount format. Keeping original amount: " + existingExpense.getAmount());
                 newAmount = existingExpense.getAmount();
            }
        }

        System.out.print("Enter new unit (or press Enter to keep '" + existingExpense.getUnit() + "'): ");
        String newUnit = scanner.nextLine();
        if (newUnit.trim().isEmpty()) newUnit = existingExpense.getUnit();

        expenseService.updateExpense(id, newTitle, newDate, newAmount, newUnit);
    }

    private static void deleteExpenseUI() {
        System.out.println("\n--- Delete Expense ---");
        viewAllExpensesUI(); // Show expenses to help user choose
        System.out.print("Enter ID of the expense to delete: ");
        int id = -1;
        try {
            id = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.err.println("Invalid ID format.");
            scanner.nextLine(); // consume invalid input
            return;
        }

        // Optional: Confirm before deletion
        Expense expenseToDelete = expenseService.getExpenseById(id);
        if (expenseToDelete != null) {
            System.out.print("Are you sure you want to delete expense '" + expenseToDelete.getTitle() + "' (ID: " + id + ")? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            if ("yes".equals(confirmation)) {
                expenseService.deleteExpense(id);
            } else {
                System.out.println("Deletion cancelled.");
            }
        } else {
             System.err.println("Expense with ID " + id + " not found.");
        }
    }

    private static void viewExpenseByIdUI() {
        System.out.println("\n--- View Expense by ID ---");
        System.out.print("Enter ID of the expense to view: ");
        int id = -1;
        try {
            id = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.err.println("Invalid ID format.");
            scanner.nextLine(); // consume invalid input
            return;
        }

        Expense expense = expenseService.getExpenseById(id);
        if (expense != null) {
            System.out.println("Details for Expense ID: " + expense.getId());
            System.out.println("Title: " + expense.getTitle());
            System.out.println("Date: " + expense.getExpenseDate().format(DATE_FORMATTER));
            System.out.println("Amount: " + expense.getAmount());
            System.out.println("Unit: " + expense.getUnit());
        } else {
            System.out.println("No expense found with ID: " + id);
        }
    }
}
