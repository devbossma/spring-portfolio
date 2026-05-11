package dev.saberlabs.myspringportfolio.investment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InvestmentEntityTest {

    private InvestmentEntity make(BigDecimal pricePerUnit, BigDecimal currentValue, int quantity) {
        InvestmentEntity inv = new InvestmentEntity();
        inv.setPricePerUnit(pricePerUnit);
        inv.setCurrentValue(currentValue);
        inv.setQuantity(quantity);
        return inv;
    }

    // ── getProfitLoss ─────────────────────────────────────────────────────────

    @Test
    void getProfitLoss_returnsPositive_whenCurrentValueExceedsPurchasePrice() {
        InvestmentEntity inv = make(new BigDecimal("100.00"), new BigDecimal("150.00"), 10);
        // (150 - 100) * 10 = 500
        assertThat(inv.getProfitLoss()).isEqualByComparingTo("500.00");
    }

    @Test
    void getProfitLoss_returnsNegative_whenCurrentValueBelowPurchasePrice() {
        InvestmentEntity inv = make(new BigDecimal("100.00"), new BigDecimal("80.00"), 5);
        // (80 - 100) * 5 = -100
        assertThat(inv.getProfitLoss()).isEqualByComparingTo("-100.00");
    }

    @Test
    void getProfitLoss_returnsZero_whenAnyFieldIsNull() {
        InvestmentEntity inv = new InvestmentEntity();
        // pricePerUnit, currentValue, quantity all null
        assertThat(inv.getProfitLoss()).isEqualByComparingTo("0");
    }

    // ── getCurrentTotalValue ──────────────────────────────────────────────────

    @Test
    void getCurrentTotalValue_returnsCurrentValueTimesQuantity() {
        InvestmentEntity inv = make(new BigDecimal("100.00"), new BigDecimal("150.00"), 10);
        // 150 * 10 = 1500
        assertThat(inv.getCurrentTotalValue()).isEqualByComparingTo("1500.00");
    }

    @Test
    void getCurrentTotalValue_returnsZero_whenCurrentValueIsNull() {
        InvestmentEntity inv = new InvestmentEntity();
        inv.setQuantity(10);
        // currentValue is null
        assertThat(inv.getCurrentTotalValue()).isEqualByComparingTo("0");
    }

    // ── getProfitLossPercentage ───────────────────────────────────────────────

    @Test
    void getProfitLossPercentage_returnsCorrectPercentage_forGain() {
        InvestmentEntity inv = make(new BigDecimal("100.00"), new BigDecimal("150.00"), 1);
        // (150 - 100) / 100 * 100 = 50.00%
        assertThat(inv.getProfitLossPercentage()).isEqualByComparingTo("50.00");
    }

    @Test
    void getProfitLossPercentage_returnsNegative_forLoss() {
        InvestmentEntity inv = make(new BigDecimal("200.00"), new BigDecimal("150.00"), 1);
        // (150 - 200) / 200 * 100 = -25.00%
        assertThat(inv.getProfitLossPercentage()).isEqualByComparingTo("-25.00");
    }

    @Test
    void getProfitLossPercentage_returnsZero_whenPricePerUnitIsZero() {
        InvestmentEntity inv = new InvestmentEntity();
        inv.setPricePerUnit(BigDecimal.ZERO);
        assertThat(inv.getProfitLossPercentage()).isEqualByComparingTo("0");
    }

    // ── status helpers ────────────────────────────────────────────────────────

    @Test
    void isPending_returnsTrue_andOthersReturnFalse_forNewInvestment() {
        // Field initializer sets status = PENDING
        InvestmentEntity inv = new InvestmentEntity();
        assertThat(inv.isPending()).isTrue();
        assertThat(inv.isActive()).isFalse();
        assertThat(inv.isExited()).isFalse();
        assertThat(inv.isFailed()).isFalse();
    }

    @Test
    void isActive_returnsTrue_whenStatusIsActive() {
        InvestmentEntity inv = new InvestmentEntity();
        inv.setStatus(InvestmentStatus.ACTIVE);
        assertThat(inv.isActive()).isTrue();
    }

    @Test
    void isExited_returnsTrue_whenStatusIsExited() {
        InvestmentEntity inv = new InvestmentEntity();
        inv.setStatus(InvestmentStatus.EXITED);
        assertThat(inv.isExited()).isTrue();
    }

    @Test
    void isFailed_returnsTrue_whenStatusIsWrittenOff() {
        InvestmentEntity inv = new InvestmentEntity();
        inv.setStatus(InvestmentStatus.WRITTEN_OFF);
        assertThat(inv.isFailed()).isTrue();
    }
}
