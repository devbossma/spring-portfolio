package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundService;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioService;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionRepository;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock private InvestmentRepository investmentRepository;
    @Mock private PortfolioService portfolioService;
    @Mock private FundService fundService;
    @Mock private InvestmentTransactionRepository investmentTransactionRepository;
    @Mock private InvestmentActivationService investmentActivationService;

    @InjectMocks
    private InvestmentService investmentService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("hashed");
    }

    private PortfolioEntity portfolioWithFund(BigDecimal totalCapital, BigDecimal deployedCapital) {
        FundEntity fund = new FundEntity();
        fund.setTotalCapital(totalCapital);
        fund.setDeployedCapital(deployedCapital);
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);
        portfolio.setFund(fund);
        return portfolio;
    }

    // ── addInvestment ─────────────────────────────────────────────────────────

    @Test
    void addInvestment_calculatesInvestedAmount_whenNotProvided() {
        PortfolioEntity portfolio = portfolioWithFund(new BigDecimal("10000.00"), BigDecimal.ZERO);

        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Test");
        inv.setPortfolio(portfolio);
        inv.setPricePerUnit(new BigDecimal("100.00"));
        inv.setQuantity(5);
        // investedAmount not set — service should calculate 100 * 5 = 500

        when(portfolioService.getPortfolioById(1L)).thenReturn(portfolio);

        investmentService.addInvestment(inv, user);

        assertThat(inv.getInvestedAmount()).isEqualByComparingTo("500.00");
        verify(investmentRepository).save(inv);
        verify(investmentTransactionRepository).save(any(InvestmentTransactionEntity.class));
        verify(portfolioService).updatePortfolioTotals(portfolio);
    }

    @Test
    void addInvestment_throwsIllegalArgument_whenInsufficientDryPowder() {
        // totalCapital=100, deployedCapital=80 → dryPowder=20; need 50
        PortfolioEntity portfolio = portfolioWithFund(new BigDecimal("100.00"), new BigDecimal("80.00"));

        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Test");
        inv.setPortfolio(portfolio);
        inv.setPricePerUnit(new BigDecimal("50.00"));
        inv.setQuantity(1);
        inv.setInvestedAmount(new BigDecimal("50.00"));

        when(portfolioService.getPortfolioById(1L)).thenReturn(portfolio);

        assertThatThrownBy(() -> investmentService.addInvestment(inv, user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient funds");
    }

    // ── updateInvestmentCurrentValue ──────────────────────────────────────────

    @Test
    void updateInvestmentCurrentValue_updatesValueAndRecalculatesTotals() {
        PortfolioEntity portfolio = portfolioWithFund(BigDecimal.ZERO, BigDecimal.ZERO);
        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Test");
        inv.setPortfolio(portfolio);
        inv.setCurrentValue(new BigDecimal("100.00"));

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(inv));

        investmentService.updateInvestmentCurrentValue(1L, new BigDecimal("150.00"));

        assertThat(inv.getCurrentValue()).isEqualByComparingTo("150.00");
        verify(investmentRepository).save(inv);
        verify(portfolioService).updatePortfolioTotals(portfolio);
    }

    // ── exitInvestment ────────────────────────────────────────────────────────

    @Test
    void exitInvestment_marksExited_andRecordsSellTransactionAndFundDeposit() {
        FundEntity fund = new FundEntity();
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);
        portfolio.setFund(fund);

        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Alpha Fund");
        inv.setPortfolio(portfolio);
        inv.setStatus(InvestmentStatus.ACTIVE);
        inv.setQuantity(10);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(inv));

        investmentService.exitInvestment(1L, new BigDecimal("2000.00"), user);

        assertThat(inv.getStatus()).isEqualTo(InvestmentStatus.EXITED);
        assertThat(inv.getExitValue()).isEqualByComparingTo("2000.00");
        assertThat(inv.getExitAt()).isNotNull();
        verify(investmentRepository).save(inv);
        verify(investmentTransactionRepository).save(any(InvestmentTransactionEntity.class));
        verify(fundService).recordFundDeposit(eq(fund), eq(new BigDecimal("2000.00")), eq(user), contains("Alpha Fund"));
        verify(portfolioService).updatePortfolioTotals(portfolio);
    }

    // ── writeOffInvestment ────────────────────────────────────────────────────

    @Test
    void writeOffInvestment_marksWrittenOff_andRecordsFundWriteOff() {
        FundEntity fund = new FundEntity();
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setId(1L);
        portfolio.setFund(fund);

        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Beta Fund");
        inv.setPortfolio(portfolio);
        inv.setStatus(InvestmentStatus.ACTIVE);
        inv.setInvestedAmount(new BigDecimal("3000.00"));
        inv.setQuantity(5);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(inv));

        investmentService.writeOffInvestment(1L, user);

        assertThat(inv.getStatus()).isEqualTo(InvestmentStatus.WRITTEN_OFF);
        assertThat(inv.getExitValue()).isEqualByComparingTo("0");
        assertThat(inv.getExitAt()).isNotNull();
        verify(investmentRepository).save(inv);
        verify(investmentTransactionRepository).save(any(InvestmentTransactionEntity.class));
        verify(fundService).recordFundWriteOff(eq(fund), eq(new BigDecimal("3000.00")), eq(user), contains("Beta Fund"));
        verify(portfolioService).updatePortfolioTotals(portfolio);
    }

    @Test
    void writeOffInvestment_throwsIllegalState_whenNotActive() {
        InvestmentEntity inv = new InvestmentEntity(); // field-initializer sets status = PENDING
        inv.setName("Test");

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(inv));

        assertThatThrownBy(() -> investmentService.writeOffInvestment(1L, user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only active investments");
    }

    // ── getInvestmentById ─────────────────────────────────────────────────────

    @Test
    void getInvestmentById_returnsInvestment_whenFound() {
        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Test");

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(inv));

        assertThat(investmentService.getInvestmentById(1L)).isSameAs(inv);
    }

    @Test
    void getInvestmentById_throwsNoSuchElement_whenNotFound() {
        when(investmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> investmentService.getInvestmentById(99L))
                .isInstanceOf(NoSuchElementException.class);
    }

    // ── updateInvestment ──────────────────────────────────────────────────────

    @Test
    void updateInvestment_savesAndUpdatesTotals() {
        PortfolioEntity portfolio = portfolioWithFund(BigDecimal.ZERO, BigDecimal.ZERO);
        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Test");
        inv.setPortfolio(portfolio);

        investmentService.updateInvestment(inv);

        verify(investmentRepository).save(inv);
        verify(portfolioService).updatePortfolioTotals(portfolio);
    }

    // ── deleteInvestment ──────────────────────────────────────────────────────

    @Test
    void deleteInvestment_deletesTransactions_whenPending() {
        PortfolioEntity portfolio = portfolioWithFund(BigDecimal.ZERO, BigDecimal.ZERO);
        InvestmentEntity inv = new InvestmentEntity(); // field-initializer sets status = PENDING
        inv.setName("Test");
        inv.setPortfolio(portfolio);
        portfolio.addInvestment(inv);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(inv));

        investmentService.deleteInvestment(1L);

        verify(investmentTransactionRepository).deleteByInvestment(inv);
        verify(investmentRepository).delete(inv);
        verify(portfolioService).updatePortfolioTotals(portfolio);
    }

    @Test
    void deleteInvestment_detachesTransactions_whenExited() {
        PortfolioEntity portfolio = portfolioWithFund(BigDecimal.ZERO, BigDecimal.ZERO);
        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Test");
        inv.setPortfolio(portfolio);
        inv.setStatus(InvestmentStatus.EXITED);
        portfolio.addInvestment(inv);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(inv));

        investmentService.deleteInvestment(1L);

        verify(investmentTransactionRepository).detachFromInvestment(inv);
        verify(investmentRepository).delete(inv);
        verify(portfolioService).updatePortfolioTotals(portfolio);
    }

    @Test
    void deleteInvestment_throwsIllegalState_whenActive() {
        InvestmentEntity inv = new InvestmentEntity();
        inv.setName("Test");
        inv.setStatus(InvestmentStatus.ACTIVE);

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(inv));

        assertThatThrownBy(() -> investmentService.deleteInvestment(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Active investments cannot be deleted");
    }
}
