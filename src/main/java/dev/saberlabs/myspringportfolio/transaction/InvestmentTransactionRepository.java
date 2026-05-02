package dev.saberlabs.myspringportfolio.transaction;

import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Bulk DELETE — executes immediately as SQL so the FK is clear before the investment row is removed.
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM InvestmentTransactionEntity t WHERE t.investment = :investment")
    void deleteByInvestment(@Param("investment") InvestmentEntity investment);

    // Nulls out the investment FK so transactions survive as an audit trail after the investment is deleted.
    @Modifying(clearAutomatically = true)
    @Query("UPDATE InvestmentTransactionEntity t SET t.investment = null WHERE t.investment = :investment")
    void detachFromInvestment(@Param("investment") InvestmentEntity investment);
}
