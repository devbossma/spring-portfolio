package dev.saberlabs.myspringportfolio.portfolio;

import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PortfolioEntityTest {

    // ── addInvestment ─────────────────────────────────────────────────────────

    @Test
    void addInvestment_addsToList_andSetsBackReference() {
        PortfolioEntity portfolio = new PortfolioEntity();
        InvestmentEntity investment = new InvestmentEntity();

        portfolio.addInvestment(investment);

        assertThat(portfolio.getInvestments()).containsExactly(investment);
        assertThat(investment.getPortfolio()).isSameAs(portfolio);
    }

    @Test
    void addInvestment_doesNothing_whenInvestmentIsNull() {
        PortfolioEntity portfolio = new PortfolioEntity();

        portfolio.addInvestment(null);

        assertThat(portfolio.getInvestments()).isEmpty();
    }

    @Test
    void addInvestment_initializesListAndAdds_whenListWasNull() {
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setInvestments(null);
        InvestmentEntity investment = new InvestmentEntity();

        portfolio.addInvestment(investment);

        assertThat(portfolio.getInvestments()).containsExactly(investment);
    }

    // ── removeInvestment ──────────────────────────────────────────────────────

    @Test
    void removeInvestment_removesFromList_andClearsBackReference() {
        PortfolioEntity portfolio = new PortfolioEntity();
        InvestmentEntity investment = new InvestmentEntity();
        investment.setId(1L);
        portfolio.addInvestment(investment);

        portfolio.removeInvestment(investment);

        assertThat(portfolio.getInvestments()).doesNotContain(investment);
        assertThat(investment.getPortfolio()).isNull();
    }

    @Test
    void removeInvestment_doesNothing_whenInvestmentIsNull() {
        PortfolioEntity portfolio = new PortfolioEntity();
        InvestmentEntity investment = new InvestmentEntity();
        investment.setId(1L);
        portfolio.addInvestment(investment);

        portfolio.removeInvestment(null);

        assertThat(portfolio.getInvestments()).containsExactly(investment);
    }

    @Test
    void removeInvestment_doesNothing_whenInvestmentIsNotInList() {
        PortfolioEntity portfolio = new PortfolioEntity();
        InvestmentEntity member = new InvestmentEntity();
        member.setId(1L);
        portfolio.addInvestment(member);

        InvestmentEntity unrelated = new InvestmentEntity();
        unrelated.setId(2L); // different ID → not equal → not removed

        portfolio.removeInvestment(unrelated);

        assertThat(portfolio.getInvestments()).containsExactly(member);
    }
}
