package dev.saberlabs.myspringportfolio.portfolio;

/*
 * Enum representing the possible lifecycle states of a portfolio.
 * - ACTIVE: The portfolio is open and accepting new investments.
 * - INACTIVE: The portfolio is temporarily paused.
 * - CLOSED: The portfolio has been permanently closed.
 * - SUSPENDED: The portfolio has been suspended (e.g. due to compliance reasons).
 * */
public enum PortfolioStatus {
    ACTIVE,
    INACTIVE,
    CLOSED,
    SUSPENDED
}
