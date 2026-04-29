package dev.saberlabs.myspringportfolio.transaction;

public enum FundTransactionType {
    DEPOSIT,    // Adding cash to the portfolio
    WITHDRAWAL, // Removing cash from the portfolio
    WRITE_OFF;  // Capital permanently lost from fund

    public static boolean isValid(FundTransactionType type) {
        return type == DEPOSIT || type == WITHDRAWAL || type == WRITE_OFF;
    }
}
