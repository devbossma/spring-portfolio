package dev.saberlabs.myspringportfolio.investment;


/*
* InvestmentStatus represents the current state of an investment in the portfolio.
* */
public enum InvestmentStatus {
    PENDING,       // Created but not yet confirmed
    ACTIVE,        // Currently held
    EXITED,        // Successfully sold
    WRITTEN_OFF    // Failed investment, capital lost
}