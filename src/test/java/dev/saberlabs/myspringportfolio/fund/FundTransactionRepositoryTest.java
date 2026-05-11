package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionRepository;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionType;
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
class FundTransactionRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private FundTransactionRepository fundTransactionRepository;

    private Long fundId;
    private Long userId;

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

        this.fundId = fund.getId();
        this.userId = user.getId();

        em.clear();
    }

    private void persistTx(BigDecimal amount, FundTransactionType type) {
        FundEntity fund = em.find(FundEntity.class, fundId);
        UserEntity user = em.find(UserEntity.class, userId);
        em.persistAndFlush(FundTransactionEntity.builder()
                .amount(amount)
                .type(type)
                .fund(fund)
                .user(user)
                .notes("test")
                .build());
    }

    // ── findByFundIdOrderByCreatedAtDesc ──────────────────────────────────────

    @Test
    void findByFundId_returnsAllTransactionsForFund() {
        persistTx(new BigDecimal("1000.00"), FundTransactionType.DEPOSIT);
        persistTx(new BigDecimal("200.00"), FundTransactionType.WITHDRAWAL);
        em.clear();

        List<FundTransactionEntity> results =
                fundTransactionRepository.findByFundIdOrderByCreatedAtDesc(fundId);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(tx -> tx.getFund().getId().equals(fundId));
    }

    @Test
    void findByFundId_returnsEmpty_whenNoTransactionsExist() {
        List<FundTransactionEntity> results =
                fundTransactionRepository.findByFundIdOrderByCreatedAtDesc(fundId);

        assertThat(results).isEmpty();
    }

    // ── findTop3ByFundIdOrderByCreatedAtDesc ──────────────────────────────────

    @Test
    void findTop3ByFundId_returnsAtMostThree_whenMoreThanThreeExist() {
        for (int i = 0; i < 5; i++) {
            persistTx(new BigDecimal("100.00"), FundTransactionType.DEPOSIT);
        }
        em.clear();

        List<FundTransactionEntity> results =
                fundTransactionRepository.findTop3ByFundIdOrderByCreatedAtDesc(fundId);

        assertThat(results).hasSize(3);
    }

    @Test
    void findTop3ByFundId_returnsAll_whenFewerThanThreeExist() {
        persistTx(new BigDecimal("100.00"), FundTransactionType.DEPOSIT);
        persistTx(new BigDecimal("200.00"), FundTransactionType.DEPOSIT);
        em.clear();

        List<FundTransactionEntity> results =
                fundTransactionRepository.findTop3ByFundIdOrderByCreatedAtDesc(fundId);

        assertThat(results).hasSize(2);
    }
}
