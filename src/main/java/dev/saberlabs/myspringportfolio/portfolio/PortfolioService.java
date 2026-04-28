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
        return portfolioRepository.findById(user.getPortfolio().getId())
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
    }

    public PortfolioEntity getPortfolioById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
    }

    public List<InvestmentEntity> getInvestmentsByPortfolio(PortfolioEntity portfolio) {
        return portfolio.getInvestments();
    }

    public FundEntity getFundByPortfolio(PortfolioEntity portfolio) {
        return portfolio.getFund();
    }

    @Transactional
    public void updatePortfolioTotals(PortfolioEntity portfolio) {
        // Use a direct query to bypass the Hibernate session cache; exclude exited/written-off (capital returned to fund)
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
