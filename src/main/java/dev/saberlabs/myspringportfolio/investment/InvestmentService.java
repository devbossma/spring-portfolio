package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundRepository;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final FundRepository fundRepository;
    private final PortfolioService portfolioService;

    public InvestmentService(InvestmentRepository investmentRepository, FundRepository fundRepository, PortfolioService portfolioService) {
        this.investmentRepository = investmentRepository;
        this.fundRepository = fundRepository;
        this.portfolioService = portfolioService;
    }

    public List<InvestmentEntity> listInvestmentsByPortfolioId(Long portfolioId) {
        return investmentRepository.findAll().stream()
                .filter(inv -> inv.getPortfolio().getId().equals(portfolioId))
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }

    @Transactional
    public void addInvestment(InvestmentEntity investment) {
        // Calculate investedAmount if not set
        if (investment.getInvestedAmount() == null) {
            investment.setInvestedAmount(investment.getPricePerUnit().multiply(java.math.BigDecimal.valueOf(investment.getQuantity())));
        }

        // Set currentValue to pricePerUnit if not provided (initially they should be the same)
        if (investment.getCurrentValue() == null) {
            investment.setCurrentValue(investment.getPricePerUnit());
        }

        // Verify sufficient balance
        BigDecimal dryPowder = investment.getPortfolio().getFund().getDryPowder();
        if (dryPowder.compareTo(investment.getInvestedAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds. Available balance: $" + dryPowder + ", Required: $" + investment.getInvestedAmount());
        }

        investmentRepository.save(investment);

        // Update portfolio totals (this will deduct from balance)
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    @Transactional
    public void updateInvestmentName(Long id, String name) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        investment.setName(name);
        investmentRepository.save(investment);
    }

    @Transactional
    public void updateInvestmentCurrentValue(Long id, BigDecimal currentValue) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        investment.setCurrentValue(currentValue);
        investmentRepository.save(investment);

        // Update portfolio totals since current value affects calculations
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    @Transactional
    public void exitInvestment(Long id, BigDecimal exitValue) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        investment.setStatus(InvestmentStatus.EXITED);
        investment.setExitValue(exitValue);
        investment.setExitAt(java.time.LocalDateTime.now());
        investmentRepository.save(investment);

        // Update portfolio totals
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    public InvestmentEntity getInvestmentById(Long id) {
        return investmentRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void updateInvestment(InvestmentEntity investment) {
        investmentRepository.save(investment);
        // Update portfolio totals since investment values may have changed
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }
}
