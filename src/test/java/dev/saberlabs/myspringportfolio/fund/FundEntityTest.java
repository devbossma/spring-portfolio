package dev.saberlabs.myspringportfolio.fund;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FundEntityTest {

    @Test
    void getDryPowder_returnsCapitalMinusDeployed() {
        FundEntity fund = new FundEntity();
        fund.setTotalCapital(new BigDecimal("1000000.00"));
        fund.setDeployedCapital(new BigDecimal("250000.00"));

        assertThat(fund.getDryPowder()).isEqualByComparingTo("750000.00");
    }

    @Test
    void getDryPowder_isZero_whenFullyDeployed() {
        FundEntity fund = new FundEntity();
        fund.setTotalCapital(new BigDecimal("500000.00"));
        fund.setDeployedCapital(new BigDecimal("500000.00"));

        assertThat(fund.getDryPowder()).isEqualByComparingTo("0");
    }

    @Test
    void getDryPowder_isNegative_whenOverdeployed() {
        FundEntity fund = new FundEntity();
        fund.setTotalCapital(new BigDecimal("100.00"));
        fund.setDeployedCapital(new BigDecimal("200.00"));

        assertThat(fund.getDryPowder()).isEqualByComparingTo("-100.00");
    }
}
