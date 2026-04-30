package dev.saberlabs.myspringportfolio.portfolio;


import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundRepository;
import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import dev.saberlabs.myspringportfolio.investment.InvestmentRepository;
import dev.saberlabs.myspringportfolio.investment.InvestmentStatus;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
/*
 * Provides business logic for retrieving and updating portfolio data.
 * Acts as the layer between the controllers and the portfolio/fund/investment repositories.
 * Also handles recalculating portfolio-level totals (totalInvested, deployedCapital) after investment changes.
 * */
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final FundRepository fundRepository;
    private final InvestmentRepository investmentRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, FundRepository fundRepository, InvestmentRepository investmentRepository) {
        this.portfolioRepository = portfolioRepository;
        this.fundRepository = fundRepository;
        this.investmentRepository = investmentRepository;
    }

    /*
     * Retrieves the portfolio for a given user using the portfolio ID stored on the user entity.
     * Params:
     * - user: The authenticated user whose portfolio is being fetched.
     * Returns: The user's PortfolioEntity. Throws IllegalArgumentException if not found.
     * */
    public PortfolioEntity getPortfolioByUser(UserEntity user) {
        return portfolioRepository.findById(user.getPortfolio().getId())
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
    }

    /*
     * Retrieves a portfolio directly by its ID.
     * Params:
     * - id: The portfolio ID.
     * Returns: The PortfolioEntity. Throws IllegalArgumentException if not found.
     * */
    public PortfolioEntity getPortfolioById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
    }

    /*
     * Returns the list of investments associated with the given portfolio.
     * Params:
     * - portfolio: The portfolio whose investments are requested.
     * Returns: A list of InvestmentEntity records belonging to this portfolio.
     * */
    public List<InvestmentEntity> getInvestmentsByPortfolio(PortfolioEntity portfolio) {
        return portfolio.getInvestments();
    }

    /*
     * Returns the fund associated with the given portfolio.
     * Params:
     * - portfolio: The portfolio whose fund is requested.
     * Returns: The FundEntity linked to this portfolio.
     * */
    public FundEntity getFundByPortfolio(PortfolioEntity portfolio) {
        return portfolio.getFund();
    }

    @Transactional
    /*
     * Recalculates and persists the portfolio's totalInvested and the fund's deployedCapital.
     * Excludes investments with EXITED or WRITTEN_OFF status from the sum, so only active
     * capital deployment is reflected in the totals.
     * Params:
     * - portfolio: The portfolio to recalculate totals for.
     * Returns: void.
     * */
    public void updatePortfolioTotals(PortfolioEntity portfolio) {
        /*
        * Calculate the total deployed amount for the portfolio, excluding investments that are either EXITED or WRITTEN_OFF.
        * */
        BigDecimal totalInvested = investmentRepository.sumDeployedAmountByPortfolioId(
                portfolio.getId(),
                Set.of(InvestmentStatus.EXITED, InvestmentStatus.WRITTEN_OFF));

        PortfolioEntity refreshedPortfolio = portfolioRepository.findById(portfolio.getId())
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

        refreshedPortfolio.setTotalInvested(totalInvested);
        refreshedPortfolio.getFund().setDeployedCapital(totalInvested);

        portfolioRepository.save(refreshedPortfolio);
        fundRepository.save(refreshedPortfolio.getFund());
    }
}
