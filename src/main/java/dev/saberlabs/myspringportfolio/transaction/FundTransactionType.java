package dev.saberlabs.myspringportfolio.transaction;

/*
 * Enum representing the types of transactions that can be performed on a fund.
 * - DEPOSIT: Cash added to the portfolio fund (e.g. initial balance or user top-up).
 * - WITHDRAWAL: Cash removed from the fund.
 * - WRITE_OFF: Capital permanently lost, typically from a written-off investment.
 * */
public enum FundTransactionType {
    DEPOSIT,    // Adding cash to the portfolio
    WITHDRAWAL, // Removing cash from the portfolio
    WRITE_OFF;  // Capital permanently lost from fund

    public static boolean isValid(FundTransactionType type) {
        return type == DEPOSIT || type == WITHDRAWAL || type == WRITE_OFF;
    }
}
