package dev.saberlabs.myspringportfolio.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentTransactionRepository extends JpaRepository<InvestmentTransactionEntity, String> {

    List<InvestmentTransactionEntity> findByInvestmentPortfolioIdOrderByCreatedAtDesc(Long portfolioId);
}
