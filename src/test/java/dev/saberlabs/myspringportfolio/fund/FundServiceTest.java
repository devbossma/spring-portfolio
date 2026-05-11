package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.transaction.FundTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionRepository;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionType;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundServiceTest {

    @Mock
    private FundRepository fundRepository;

    @Mock
    private FundTransactionRepository fundTransactionRepository;

    @InjectMocks
    private FundService fundService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setUsername("Yassine");
        user.setEmail("yassine@example.com");
        user.setPassword("hashed_password");
    }

    private FundEntity fundWith(BigDecimal totalCapital, BigDecimal deployedCapital) {
        FundEntity fund = new FundEntity();
        fund.setTotalCapital(totalCapital);
        fund.setDeployedCapital(deployedCapital);
        return fund;
    }

    private FundTransactionEntity captureLastSavedTransaction() {
        ArgumentCaptor<FundTransactionEntity> captor = ArgumentCaptor.forClass(FundTransactionEntity.class);
        verify(fundTransactionRepository).save(captor.capture());
        return captor.getValue();
    }

    //  recordInitialBalance 

    @Test
    void recordInitialBalance_setsFundCapitalTo10M_andRecordsDepositTransaction() {
        FundEntity fund = new FundEntity();

        fundService.recordInitialBalance(fund, user);

        assertThat(fund.getTotalCapital()).isEqualByComparingTo("10000000");
        verify(fundRepository).save(fund);

        FundTransactionEntity tx = captureLastSavedTransaction();
        assertThat(tx.getType()).isEqualTo(FundTransactionType.DEPOSIT);
        assertThat(tx.getAmount()).isEqualByComparingTo("10000000");
        assertThat(tx.getNotes()).isEqualTo("Initial Balance");
    }

    //  addToFund

    @Test
    void addToFund_increasesTotalCapital_andRecordsDepositTransaction() {
        FundEntity fund = fundWith(new BigDecimal("1000.00"), BigDecimal.ZERO);
        when(fundRepository.findById(1L)).thenReturn(Optional.of(fund));

        fundService.addToFund(1L, new BigDecimal("500.00"), user);

        assertThat(fund.getTotalCapital()).isEqualByComparingTo("1500.00");
        verify(fundRepository).save(fund);

        FundTransactionEntity tx = captureLastSavedTransaction();
        assertThat(tx.getType()).isEqualTo(FundTransactionType.DEPOSIT);
        assertThat(tx.getAmount()).isEqualByComparingTo("500.00");
    }

    //  withdraw 

    @Test
    void withdraw_decreasesTotalCapital_andRecordsWithdrawalTransaction() {
        FundEntity fund = fundWith(new BigDecimal("1000.00"), BigDecimal.ZERO);
        when(fundRepository.findById(1L)).thenReturn(Optional.of(fund));

        fundService.withdraw(1L, new BigDecimal("300.00"), user);

        assertThat(fund.getTotalCapital()).isEqualByComparingTo("700.00");
        verify(fundRepository).save(fund);

        FundTransactionEntity tx = captureLastSavedTransaction();
        assertThat(tx.getType()).isEqualTo(FundTransactionType.WITHDRAWAL);
        assertThat(tx.getAmount()).isEqualByComparingTo("300.00");
    }

    @Test
    void withdraw_throwsIllegalArgument_whenAmountExceedsDryPowder() {
        // totalCapital=100, deployedCapital=80 → dryPowder=20; trying to withdraw 50
        FundEntity fund = fundWith(new BigDecimal("100.00"), new BigDecimal("80.00"));
        when(fundRepository.findById(1L)).thenReturn(Optional.of(fund));

        assertThatThrownBy(() -> fundService.withdraw(1L, new BigDecimal("50.00"), user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dry powder");
    }

    //  recordFundWriteOff 

    @Test
    void recordFundWriteOff_decreasesTotalCapital_andRecordsWriteOffTransaction() {
        FundEntity fund = fundWith(new BigDecimal("5000.00"), BigDecimal.ZERO);

        fundService.recordFundWriteOff(fund, new BigDecimal("2000.00"), user, "Investment failed");

        assertThat(fund.getTotalCapital()).isEqualByComparingTo("3000.00");
        verify(fundRepository).save(fund);

        FundTransactionEntity tx = captureLastSavedTransaction();
        assertThat(tx.getType()).isEqualTo(FundTransactionType.WRITE_OFF);
        assertThat(tx.getAmount()).isEqualByComparingTo("2000.00");
        assertThat(tx.getNotes()).isEqualTo("Investment failed");
    }

    //  recordFundDeposit

    @Test
    void recordFundDeposit_increasesTotalCapital_andRecordsDepositTransaction() {
        FundEntity fund = fundWith(new BigDecimal("3000.00"), BigDecimal.ZERO);

        fundService.recordFundDeposit(fund, new BigDecimal("1500.00"), user, "Exit proceeds");

        assertThat(fund.getTotalCapital()).isEqualByComparingTo("4500.00");
        verify(fundRepository).save(fund);

        FundTransactionEntity tx = captureLastSavedTransaction();
        assertThat(tx.getType()).isEqualTo(FundTransactionType.DEPOSIT);
        assertThat(tx.getAmount()).isEqualByComparingTo("1500.00");
        assertThat(tx.getNotes()).isEqualTo("Exit proceeds");
    }
}
