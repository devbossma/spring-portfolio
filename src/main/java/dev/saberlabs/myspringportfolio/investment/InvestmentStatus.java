package dev.saberlabs.myspringportfolio.investment;

public enum InvestmentStatus {
    PENDING,       // Created but not yet confirmed
    ACTIVE,        // Currently held
    EXITED,        // Successfully sold
    WRITTEN_OFF    // Failed investment, capital lost
}