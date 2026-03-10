package dev.saberlabs.myspringportfolio.transaction;

public enum FundTransactionType {
    DEPOSIT,    // Adding cash to the portfolio
    WITHDRAWAL; // Removing cash from the portfolio

    public static boolean isValid(FundTransactionType type) {
        return type == DEPOSIT || type == WITHDRAWAL;
    }
}
