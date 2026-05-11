package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InvestmentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private InvestmentRepository investmentRepository;

    private Long portfolioId;

    @BeforeEach
    void setUp() {
        FundEntity fund = new FundEntity();
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setFund(fund);

        UserEntity user = new UserEntity();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("hashed");
        user.setPortfolio(portfolio);

        em.persistAndFlush(user);
        this.portfolioId = portfolio.getId();
        em.clear();
    }

    private InvestmentEntity persistInvestment(InvestmentStatus status, BigDecimal investedAmount) {
        PortfolioEntity portfolio = em.find(PortfolioEntity.class, portfolioId);
        InvestmentEntity inv = InvestmentEntity.builder()
                .name("Test Investment")
                .status(status)
                .investedAmount(investedAmount)
                .pricePerUnit(new BigDecimal("100.00"))
                .currentValue(new BigDecimal("100.00"))
                .quantity(1)
                .portfolio(portfolio)
                .build();
        return em.persistAndFlush(inv);
    }

    // ── findByStatus ──────────────────────────────────────────────────────────

    @Test
    void findByStatus_returnsMatchingInvestments() {
        persistInvestment(InvestmentStatus.PENDING, new BigDecimal("1000.00"));
        persistInvestment(InvestmentStatus.PENDING, new BigDecimal("500.00"));
        persistInvestment(InvestmentStatus.ACTIVE, new BigDecimal("2000.00"));
        em.clear();

        List<InvestmentEntity> results = investmentRepository.findByStatus(InvestmentStatus.PENDING);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(inv -> inv.getStatus() == InvestmentStatus.PENDING);
    }

    @Test
    void findByStatus_returnsEmpty_whenNoneMatch() {
        persistInvestment(InvestmentStatus.PENDING, new BigDecimal("1000.00"));
        em.clear();

        List<InvestmentEntity> results = investmentRepository.findByStatus(InvestmentStatus.ACTIVE);

        assertThat(results).isEmpty();
    }

    // ── sumDeployedAmountByPortfolioId ────────────────────────────────────────

    @Test
    void sumDeployedAmount_sumsActiveAndPending_excludingExitedAndWrittenOff() {
        persistInvestment(InvestmentStatus.ACTIVE, new BigDecimal("1000.00"));
        persistInvestment(InvestmentStatus.PENDING, new BigDecimal("500.00"));
        persistInvestment(InvestmentStatus.EXITED, new BigDecimal("2000.00"));
        em.clear();

        Set<InvestmentStatus> excluded = Set.of(InvestmentStatus.EXITED, InvestmentStatus.WRITTEN_OFF);
        BigDecimal result = investmentRepository.sumDeployedAmountByPortfolioId(portfolioId, excluded);

        assertThat(result).isEqualByComparingTo("1500.00");
    }

    @Test
    void sumDeployedAmount_returnsZero_whenAllInvestmentsAreExcluded() {
        persistInvestment(InvestmentStatus.EXITED, new BigDecimal("2000.00"));
        persistInvestment(InvestmentStatus.WRITTEN_OFF, new BigDecimal("500.00"));
        em.clear();

        Set<InvestmentStatus> excluded = Set.of(InvestmentStatus.EXITED, InvestmentStatus.WRITTEN_OFF);
        BigDecimal result = investmentRepository.sumDeployedAmountByPortfolioId(portfolioId, excluded);

        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void sumDeployedAmount_returnsZero_whenNoInvestmentsExist() {
        Set<InvestmentStatus> excluded = Set.of(InvestmentStatus.EXITED, InvestmentStatus.WRITTEN_OFF);
        BigDecimal result = investmentRepository.sumDeployedAmountByPortfolioId(portfolioId, excluded);

        assertThat(result).isEqualByComparingTo("0");
    }
}
