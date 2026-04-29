package dev.saberlabs.myspringportfolio.investment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<InvestmentEntity, Long> {

    List<InvestmentEntity> findByStatus(InvestmentStatus status);

    @Query("SELECT COALESCE(SUM(i.investedAmount), 0) FROM InvestmentEntity i WHERE i.portfolio.id = :portfolioId AND i.status NOT IN :excludedStatuses")
    BigDecimal sumDeployedAmountByPortfolioId(@Param("portfolioId") Long portfolioId,
                                              @Param("excludedStatuses") Collection<InvestmentStatus> excludedStatuses);

}
