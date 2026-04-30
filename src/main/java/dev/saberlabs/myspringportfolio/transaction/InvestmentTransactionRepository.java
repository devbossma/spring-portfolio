package dev.saberlabs.myspringportfolio.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/*
 * Spring Data JPA repository for InvestmentTransactionEntity.
 * Provides standard CRUD and a custom query for fetching all investment transactions
 * belonging to a specific portfolio, ordered by creation date descending (most recent first).
 * */
public interface InvestmentTransactionRepository extends JpaRepository<InvestmentTransactionEntity, String> {

    List<InvestmentTransactionEntity> findByInvestmentPortfolioIdOrderByCreatedAtDesc(Long portfolioId);
}
