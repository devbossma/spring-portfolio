package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionRepository;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionType;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InvestmentTransactionRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private InvestmentTransactionRepository investmentTransactionRepository;

    private Long userId;
    private Long investmentId;

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
        this.userId = user.getId();
        Long portfolioId = portfolio.getId();
        em.clear();

        PortfolioEntity managedPortfolio = em.find(PortfolioEntity.class, portfolioId);
        InvestmentEntity inv = InvestmentEntity.builder()
                .name("Test Investment")
                .status(InvestmentStatus.ACTIVE)
                .investedAmount(new BigDecimal("1000.00"))
                .pricePerUnit(new BigDecimal("100.00"))
                .currentValue(new BigDecimal("100.00"))
                .quantity(1)
                .portfolio(managedPortfolio)
                .build();
        em.persistAndFlush(inv);
        this.investmentId = inv.getId();
        em.clear();
    }

    private String persistTx(InvestmentEntity investment, UserEntity user) {
        InvestmentTransactionEntity tx = InvestmentTransactionEntity.builder()
                .amount(new BigDecimal("1000.00"))
                .type(InvestmentTransactionType.BUY)
                .investment(investment)
                .user(user)
                .notes("test")
                .build();
        em.persistAndFlush(tx);
        return tx.getId();
    }

    // ── findByUserIdOrderByCreatedAtDesc ──────────────────────────────────────

    @Test
    void findByUserId_returnsAllTransactionsForUser() {
        InvestmentEntity inv = em.find(InvestmentEntity.class, investmentId);
        UserEntity user = em.find(UserEntity.class, userId);
        persistTx(inv, user);
        persistTx(inv, user);
        em.clear();

        List<InvestmentTransactionEntity> results =
                investmentTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(tx -> tx.getUser().getId().equals(userId));
    }

    @Test
    void findByUserId_returnsEmpty_whenNoTransactionsExist() {
        List<InvestmentTransactionEntity> results =
                investmentTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);

        assertThat(results).isEmpty();
    }

    // ── deleteByInvestment ────────────────────────────────────────────────────

    @Test
    void deleteByInvestment_removesLinkedTransactions() {
        InvestmentEntity inv = em.find(InvestmentEntity.class, investmentId);
        UserEntity user = em.find(UserEntity.class, userId);
        persistTx(inv, user);
        em.clear();

        inv = em.find(InvestmentEntity.class, investmentId);
        investmentTransactionRepository.deleteByInvestment(inv);
        em.clear();

        List<InvestmentTransactionEntity> remaining =
                investmentTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        assertThat(remaining).isEmpty();
    }

    // ── detachFromInvestment ──────────────────────────────────────────────────

    @Test
    void detachFromInvestment_nullsInvestmentFK_butTransactionSurvives() {
        InvestmentEntity inv = em.find(InvestmentEntity.class, investmentId);
        UserEntity user = em.find(UserEntity.class, userId);
        String txId = persistTx(inv, user);
        em.clear();

        inv = em.find(InvestmentEntity.class, investmentId);
        investmentTransactionRepository.detachFromInvestment(inv);
        em.clear();

        InvestmentTransactionEntity tx = em.find(InvestmentTransactionEntity.class, txId);
        assertThat(tx).isNotNull();
        assertThat(tx.getInvestment()).isNull();
    }
}
