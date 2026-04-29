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
import java.time.Instant;
import java.util.List;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final PortfolioService portfolioService;
    private final FundService fundService;
    private final InvestmentTransactionRepository investmentTransactionRepository;
    private final InvestmentActivationService investmentActivationService;

    public InvestmentService(InvestmentRepository investmentRepository,
                             PortfolioService portfolioService,
                             FundService fundService,
                             InvestmentTransactionRepository investmentTransactionRepository,
                             InvestmentActivationService investmentActivationService) {
        this.investmentRepository = investmentRepository;
        this.portfolioService = portfolioService;
        this.fundService = fundService;
        this.investmentTransactionRepository = investmentTransactionRepository;
        this.investmentActivationService = investmentActivationService;
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

        // Reloading the portfolio and fund from DB to get accurate balance.
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

        investmentActivationService.scheduleActivation(
                investment.getId(), user.getId(),
                Instant.now().plusSeconds(InvestmentActivationService.ACTIVATION_DELAY_SECONDS));

        // Record BUY transaction
        investmentTransactionRepository.save(InvestmentTransactionEntity.builder()
                .amount(investment.getInvestedAmount())
                .user(user)
                .type(InvestmentTransactionType.BUY)
                .investment(investment)
                .pricePerUnit(investment.getPricePerUnit())
                .quantity(investment.getQuantity())
                .notes("Investment Purchase")
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
                .notes("Investment Exit")
                .build());

        // Return exit proceeds to the fund and record a DEPOSIT fund transaction
        FundEntity fund = investment.getPortfolio().getFund();
        fundService.recordFundDeposit(fund, exitValue, user,
                "Exit proceeds from: " + investment.getName());

        // Recalculate deployedCapital.
        portfolioService.updatePortfolioTotals(investment.getPortfolio());
    }

    @Transactional
    public void writeOffInvestment(Long id, UserEntity user) {
        InvestmentEntity investment = investmentRepository.findById(id).orElseThrow();
        if (!investment.isActive()) {
            throw new IllegalStateException("Only active investments can be written off.");
        }
        investment.setStatus(InvestmentStatus.WRITTEN_OFF);
        investment.setExitValue(BigDecimal.ZERO);
        investment.setExitAt(java.time.LocalDateTime.now());
        investmentRepository.save(investment);

        investmentTransactionRepository.save(InvestmentTransactionEntity.builder()
                .amount(investment.getInvestedAmount())
                .user(user)
                .type(dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionType.WRITE_OFF)
                .investment(investment)
                .pricePerUnit(BigDecimal.ZERO)
                .quantity(investment.getQuantity())
                .notes("Investment Written Off")
                .build());

        FundEntity fund = investment.getPortfolio().getFund();
        fundService.recordFundWriteOff(fund, investment.getInvestedAmount(), user,
                "Write-off: " + investment.getName());

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
