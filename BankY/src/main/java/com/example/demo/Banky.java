package com.example.demo;

import java.sql.*;
import java.util.Scanner;

public class Banky {
    private static final String databaseURL = "jdbc:mysql://localhost:3306/Banky";
    private static final String databaseUSER = "root";  
    private static final String databasePASSWORD = "root"; 

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(databaseURL, databaseUSER, databasePASSWORD)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("-------------------------------------------------");
            System.out.println("********WELCOME TO RADHIKA REDDY BANKY***********");
            System.out.println("-------------------------------------------------");

            while (true) {
                System.out.println("\n***WELCOME***");
                System.out.println("\n****YOU CAN PERFORM BELOW OPERATIONS****");
                System.out.println("* 1. Create Account             *");
                System.out.println("* 2. Deposit Funds              *");
                System.out.println("* 3. Withdraw Funds             *");
                System.out.println("* 4. Transfer Funds             *");
                System.out.println("* 5. Check Balance              *");
                System.out.println("* 6. Exit                       *");
                System.out.println("-----------------------------------------");
                System.out.print("Choose an option: ");
                int option = scanner.nextInt();
                scanner.nextLine();
                if (option == 1) {
                    createAccount(scanner, conn);
                } else if (option == 2) {
                    depositFunds(scanner, conn);
                } else if (option == 3) {
                    withdrawFunds(scanner, conn);
                } else if (option == 4) {
                    transferFunds(scanner, conn);
                } else if (option == 5) {
                    checkBalance(scanner, conn);
                } else if (option == 6) {
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    private static void createAccount(Scanner scanner, Connection conn) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter account holder name: ");
        String accountHolder = scanner.nextLine();
        System.out.print("Enter security PIN: ");
        int securityPin = scanner.nextInt();
        scanner.nextLine();

        String sql = "INSERT INTO accounts (account_number, account_holder, security_pin, balance) VALUES (?, ?, ?, 0)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, accountHolder);
            pstmt.setInt(3, securityPin);
            pstmt.executeUpdate();
            System.out.println("Account created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private static void depositFunds(Scanner scanner, Connection conn) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter security PIN: ");
        int securityPin = scanner.nextInt();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ? AND security_pin = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountNumber);
            pstmt.setInt(3, securityPin);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Deposit successful.");
            } else {
                System.out.println("Account not found or invalid PIN.");
            }
        } catch (SQLException e) {
            System.out.println("Error depositing funds: " + e.getMessage());
        }
    }

    private static void withdrawFunds(Scanner scanner, Connection conn) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter security PIN: ");
        int securityPin = scanner.nextInt();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        String sql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND security_pin = ? AND balance >= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountNumber);
            pstmt.setInt(3, securityPin);
            pstmt.setDouble(4, amount);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Account not found, invalid PIN, or insufficient balance.");
            }
        } catch (SQLException e) {
            System.out.println("Error withdrawing funds: " + e.getMessage());
        }
    }

    private static void transferFunds(Scanner scanner, Connection conn) {
        System.out.print("Enter your account number: ");
        String senderAccountNumber = scanner.nextLine();
        System.out.print("Enter security PIN: ");
        int senderPin = scanner.nextInt();
        scanner.nextLine();  
        System.out.print("Enter the recipient's account number: ");
        String recipientAccountNumber = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  

        try {
            conn.setAutoCommit(false); // Start transaction

            // Check sender balance and update sender account
            String sqlSender = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? AND security_pin = ? AND balance >= ?";
            try (PreparedStatement pstmtSender = conn.prepareStatement(sqlSender)) {
                pstmtSender.setDouble(1, amount);
                pstmtSender.setString(2, senderAccountNumber);
                pstmtSender.setInt(3, senderPin);
                pstmtSender.setDouble(4, amount);
                int rowsAffectedSender = pstmtSender.executeUpdate();
                if (rowsAffectedSender == 0) {
                    conn.rollback();
                    System.out.println("Sender account not found, invalid PIN, or insufficient balance.");
                    return;
                }
            }

            // Check if recipient exists and update or create recipient account
            String sqlRecipient = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            try (PreparedStatement pstmtRecipient = conn.prepareStatement(sqlRecipient)) {
                pstmtRecipient.setDouble(1, amount);
                pstmtRecipient.setString(2, recipientAccountNumber);
                int rowsAffectedRecipient = pstmtRecipient.executeUpdate();
                if (rowsAffectedRecipient == 0) {
                    // Recipient does not exist, create new account
                    String sqlCreateRecipient = "INSERT INTO accounts (account_number, account_holder, security_pin, balance) VALUES (?, 'Unknown', 0, ?)";
                    try (PreparedStatement pstmtCreateRecipient = conn.prepareStatement(sqlCreateRecipient)) {
                        pstmtCreateRecipient.setString(1, recipientAccountNumber);
                        pstmtCreateRecipient.setDouble(2, amount);
                        pstmtCreateRecipient.executeUpdate();
                    }
                }
            }

            conn.commit(); // Commit transaction
            System.out.println("Transfer successful.");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.out.println("Error transferring funds: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true); // Restore default auto-commit behavior
            } catch (SQLException e) {
                System.out.println("Error restoring auto-commit: " + e.getMessage());
            }
        }
    }

    private static void checkBalance(Scanner scanner, Connection conn) {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter security PIN: ");
        int securityPin = scanner.nextInt();
        scanner.nextLine();

        String sql = "SELECT account_holder, balance FROM accounts WHERE account_number = ? AND security_pin = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setInt(2, securityPin);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String accountHolder = rs.getString("account_holder");
                    double balance = rs.getDouble("balance");
                    System.out.println("Account Holder: " + accountHolder);
                    System.out.println("Balance: " + balance);
                } else {
                    System.out.println("Account not found or invalid PIN.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking balance: " + e.getMessage());
        }
    }
}
