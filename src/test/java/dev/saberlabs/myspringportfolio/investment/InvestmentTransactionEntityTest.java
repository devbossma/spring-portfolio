package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvestmentTransactionEntityTest {

    private InvestmentTransactionEntity buildSell(int quantity) {
        return InvestmentTransactionEntity.builder()
                .type(InvestmentTransactionType.SELL)
                .quantity(quantity)
                .amount(BigDecimal.TEN)
                .build();
    }

    // ── validateSell ──────────────────────────────────────────────────────────

    @Test
    void validateSell_doesNotThrow_whenQuantityWithinLimit() {
        InvestmentTransactionEntity tx = buildSell(5);
        assertThatCode(() -> tx.validateSell(10)).doesNotThrowAnyException();
    }

    @Test
    void validateSell_throwsIllegalState_whenQuantityExceedsHeld() {
        InvestmentTransactionEntity tx = buildSell(15);
        assertThatThrownBy(() -> tx.validateSell(10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot sell");
    }

    @Test
    void validateSell_skipsCheck_forNonSellType() {
        // quantity would fail the SELL check, but type is BUY so the check is skipped
        InvestmentTransactionEntity tx = InvestmentTransactionEntity.builder()
                .type(InvestmentTransactionType.BUY)
                .quantity(15)
                .amount(BigDecimal.TEN)
                .build();
        assertThatCode(() -> tx.validateSell(5)).doesNotThrowAnyException();
    }

    // ── getTotalValue ─────────────────────────────────────────────────────────

    @Test
    void getTotalValue_returnsPricePerUnitTimesQuantity() {
        // 100 * 5 = 500
        InvestmentTransactionEntity tx = InvestmentTransactionEntity.builder()
                .type(InvestmentTransactionType.BUY)
                .pricePerUnit(new BigDecimal("100.00"))
                .quantity(5)
                .amount(new BigDecimal("500.00"))
                .build();
        assertThat(tx.getTotalValue()).isEqualByComparingTo("500.00");
    }

    @Test
    void getTotalValue_returnsZero_whenFieldsAreNull() {
        InvestmentTransactionEntity tx = new InvestmentTransactionEntity();
        // pricePerUnit and quantity are null from no-arg constructor
        assertThat(tx.getTotalValue()).isEqualByComparingTo("0");
    }

    // ── setType ───────────────────────────────────────────────────────────────

    @Test
    void setType_throwsIllegalArgument_whenNull() {
        InvestmentTransactionEntity tx = new InvestmentTransactionEntity();
        assertThatThrownBy(() -> tx.setType(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    // ── getTransactionCategory ────────────────────────────────────────────────

    @Test
    void getTransactionCategory_returnsInvestment() {
        assertThat(new InvestmentTransactionEntity().getTransactionCategory()).isEqualTo("INVESTMENT");
    }
}
