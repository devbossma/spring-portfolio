package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.transaction.FundTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FundTransactionEntityTest {

    // ── validateWithdrawal ────────────────────────────────────────────────────

    @Test
    void validateWithdrawal_doesNotThrow_whenAmountIsWithinBalance() {
        FundTransactionEntity tx = new FundTransactionEntity();
        tx.setAmount(new BigDecimal("100.00"));
        tx.setType(FundTransactionType.WITHDRAWAL);

        assertThatNoException().isThrownBy(() -> tx.validateWithdrawal(new BigDecimal("200.00")));
    }

    @Test
    void validateWithdrawal_throws_whenAmountExceedsBalance() {
        FundTransactionEntity tx = new FundTransactionEntity();
        tx.setAmount(new BigDecimal("300.00"));
        tx.setType(FundTransactionType.WITHDRAWAL);

        assertThatThrownBy(() -> tx.validateWithdrawal(new BigDecimal("200.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("300.00")
                .hasMessageContaining("200.00");
    }

    @Test
    void validateWithdrawal_doesNotThrow_forNonWithdrawalType() {
        // DEPOSIT with a huge amount vs a tiny balance — check is skipped for non-WITHDRAWAL types
        FundTransactionEntity tx = new FundTransactionEntity();
        tx.setAmount(new BigDecimal("999999.00"));
        tx.setType(FundTransactionType.DEPOSIT);

        assertThatNoException().isThrownBy(() -> tx.validateWithdrawal(new BigDecimal("1.00")));
    }

    // ── setType ───────────────────────────────────────────────────────────────

    @Test
    void setType_throwsIllegalArgument_whenTypeIsNull() {
        FundTransactionEntity tx = new FundTransactionEntity();

        assertThatThrownBy(() -> tx.setType(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    // ── getTransactionCategory ────────────────────────────────────────────────

    @Test
    void getTransactionCategory_returnsFund() {
        assertThat(new FundTransactionEntity().getTransactionCategory()).isEqualTo("FUND");
    }
}
