package dev.saberlabs.myspringportfolio.transaction;

public enum InvestmentTransactionType {
    BUY,       // Buying an investment
    SELL,      // Selling an investment
    WRITE_OFF; // Capital permanently lost

    public static boolean isValid(InvestmentTransactionType type) {
        return type == BUY || type == SELL || type == WRITE_OFF;
    }
}
