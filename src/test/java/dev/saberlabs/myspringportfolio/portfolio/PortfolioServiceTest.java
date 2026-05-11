package dev.saberlabs.myspringportfolio.portfolio;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundRepository;
import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import dev.saberlabs.myspringportfolio.investment.InvestmentRepository;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private FundRepository fundRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    // getPortfolioByUser 

    @Test
    void getPortfolioByUser_returnsPortfolio_whenFound() {
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);

        UserEntity user = new UserEntity();
        user.setPortfolio(portfolio);

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        PortfolioEntity result = portfolioService.getPortfolioByUser(user);

        assertThat(result).isSameAs(portfolio);
    }

    @Test
    void getPortfolioByUser_throwsIllegalArgument_whenNotFound() {
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(99L);

        UserEntity user = new UserEntity();
        user.setPortfolio(portfolio);

        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portfolioService.getPortfolioByUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Portfolio not found");
    }

    // getPortfolioById

    @Test
    void getPortfolioById_returnsPortfolio_whenFound() {
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        PortfolioEntity result = portfolioService.getPortfolioById(1L);

        assertThat(result).isSameAs(portfolio);
    }

    @Test
    void getPortfolioById_throwsIllegalArgument_whenNotFound() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portfolioService.getPortfolioById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Portfolio not found");
    }

    // getInvestmentsByPortfolio

    @Test
    void getInvestmentsByPortfolio_returnsList() {
        PortfolioEntity portfolio = new PortfolioEntity();
        InvestmentEntity inv = new InvestmentEntity();
        portfolio.getInvestments().add(inv);

        List<InvestmentEntity> result = portfolioService.getInvestmentsByPortfolio(portfolio);

        assertThat(result).containsExactly(inv);
    }

    // getFundByPortfolio 

    @Test
    void getFundByPortfolio_returnsFund() {
        FundEntity fund = new FundEntity();
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setFund(fund);

        FundEntity result = portfolioService.getFundByPortfolio(portfolio);

        assertThat(result).isSameAs(fund);
    }

    // updatePortfolioTotals

    @Test
    void updatePortfolioTotals_setsCorrectTotals_andSavesBothEntities() {
        FundEntity fund = new FundEntity();
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);
        portfolio.setFund(fund);

        when(investmentRepository.sumDeployedAmountByPortfolioId(eq(1L), anySet()))
                .thenReturn(new BigDecimal("5000.00"));
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        portfolioService.updatePortfolioTotals(portfolio);

        assertThat(portfolio.getTotalInvested()).isEqualByComparingTo("5000.00");
        assertThat(fund.getDeployedCapital()).isEqualByComparingTo("5000.00");
        verify(portfolioRepository).save(portfolio);
        verify(fundRepository).save(fund);
    }
}
