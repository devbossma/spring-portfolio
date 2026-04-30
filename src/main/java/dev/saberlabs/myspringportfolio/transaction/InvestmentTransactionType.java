package dev.saberlabs.myspringportfolio.transaction;

/*
 * Enum representing the types of transactions that can be performed on an investment.
 * - BUY: Capital deployed into an investment.
 * - SELL: Capital returned from an investment (partial or full exit).
 * - WRITE_OFF: Capital permanently lost from an investment.
 * */
public enum InvestmentTransactionType {
    BUY,       // Buying an investment
    SELL,      // Selling an investment
    WRITE_OFF; // Capital permanently lost

    public static boolean isValid(InvestmentTransactionType type) {
        return type == BUY || type == SELL || type == WRITE_OFF;
    }
}
