package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundService;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioService;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionRepository;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionRepository;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionType;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final PortfolioService portfolioService;
    private final FundService fundService;
    private final InvestmentTransactionRepository investmentTransactionRepository;

    public InvestmentService(InvestmentRepository investmentRepository,
                             PortfolioService portfolioService,
                             FundService fundService,
                             InvestmentTransactionRepository investmentTransactionRepository) {
        this.investmentRepository = investmentRepository;
        this.portfolioService = portfolioService;
        this.fundService = fundService;
        this.investmentTransactionRepository = investmentTransactionRepository;
    }

    public List<InvestmentEntity> listInvestmentsByPortfolioId(Long portfolioId) {
        return investmentRepository.findAll().stream()
                .filter(inv -> inv.getPortfolio().getId().equals(portfolioId))
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }

    @Transactional
    public void addInvestment(InvestmentEntity investment, UserEntity user) {
        if (investment.getInvestedAmount() == null) {
            investment.setInvestedAmount(
                    investment.getPricePerUnit().multiply(BigDecimal.valueOf(investment.getQuantity())));
        }

        // Reload portfolio and fund from DB to get accurate balance (session object may be stale)
        PortfolioEntity freshPortfolio = portfolioService.getPortfolioById(investment.getPortfolio().getId());
        FundEntity fund = freshPortfolio.getFund();
        BigDecimal dryPowder = fund.getDryPowder();
        if (dryPowder.compareTo(investment.getInvestedAmount()) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient funds. Available: $" + dryPowder + ", Required: $" + investment.getInvestedAmount());
        }

        if (investment.getCurrentValue() == null) {
            investment.setCurrentValue(investment.getPricePerUnit());
        }

        investmentRepository.save(investment);

        // Record BUY transaction
        investmentTransactionRepository.save(InvestmentTransactionEntity.builder()
                .amount(investment.getInvestedAmount())
                .user(user)
                .type(InvestmentTransactionType.BUY)
                .investment(investment)
                .pricePerUnit(investment.getPricePerUnit())
                .quantity(investment.getQuantity())
                .notes("Investment purchase")
                .build());

        portfolioService.updatePortfolioTotals(freshPortfolio);
    }

    @Transactional
    public void updateInvestmentCurrentValue(Long id, BigDecimal currentValue) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        investment.setCurrentValue(currentValue);
        investmentRepository.save(investment);
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    @Transactional
    public void exitInvestment(Long id, BigDecimal exitValue, UserEntity user) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        investment.setStatus(InvestmentStatus.EXITED);
        investment.setExitValue(exitValue);
        investment.setExitAt(java.time.LocalDateTime.now());
        investmentRepository.save(investment);

        // Record SELL investment transaction
        investmentTransactionRepository.save(InvestmentTransactionEntity.builder()
                .amount(exitValue)
                .user(user)
                .type(InvestmentTransactionType.SELL)
                .investment(investment)
                .pricePerUnit(exitValue.divide(BigDecimal.valueOf(investment.getQuantity()), 2, java.math.RoundingMode.HALF_UP))
                .quantity(investment.getQuantity())
                .notes("Investment exit")
                .build());

        // Return exit proceeds to the fund and record a DEPOSIT fund transaction
        FundEntity fund = investment.getPortfolio().getFund();
        fundService.recordFundDeposit(fund, exitValue, user,
                "Exit proceeds from: " + investment.getName());

        // Recalculate deployedCapital (exited investment is now excluded)
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    public InvestmentEntity getInvestmentById(Long id) {
        return investmentRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void updateInvestment(InvestmentEntity investment) {
        investmentRepository.save(investment);
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }
}
