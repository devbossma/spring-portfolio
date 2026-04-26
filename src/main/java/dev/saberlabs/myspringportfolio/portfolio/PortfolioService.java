package dev.saberlabs.myspringportfolio.portfolio;


import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundRepository;
import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import dev.saberlabs.myspringportfolio.investment.InvestmentRepository;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final FundRepository fundRepository;
    private final InvestmentRepository investmentRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, FundRepository fundRepository, InvestmentRepository investmentRepository) {
        this.portfolioRepository = portfolioRepository;
        this.fundRepository = fundRepository;
        this.investmentRepository = investmentRepository;
    }

    public PortfolioEntity getPortfolioByUser(UserEntity user) {
        return user.getPortfolio();
    }

    public List<InvestmentEntity> getInvestmentsByPortfolio(PortfolioEntity portfolio) {
        return portfolio.getInvestments();
    }

    public FundEntity getFundByPortfolio(PortfolioEntity portfolio) {
        return portfolio.getFund();
    }

    @Transactional
    public void updatePortfolioTotals(PortfolioEntity portfolio) {
        BigDecimal totalInvested = portfolio.getInvestments().stream()
                .map(InvestmentEntity::getInvestedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        portfolio.setTotalInvested(totalInvested);

        BigDecimal deployedCapital = totalInvested;
        portfolio.getFund().setDeployedCapital(deployedCapital);

        portfolioRepository.save(portfolio);
        fundRepository.save(portfolio.getFund());
    }
}
