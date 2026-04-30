package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.transaction.FundTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionRepository;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionType;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
/*
 * Manages all fund-level financial operations: initial balance setup, capital deposits,
 * withdrawals, write-offs, and fund-level deposit recording.
 * Each operation is transactional and records a corresponding FundTransactionEntity for the audit ledger.
 * */
public class FundService {

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(10_000_000L);

    private final FundRepository fundRepository;
    private final FundTransactionRepository fundTransactionRepository;

    public FundService(FundRepository fundRepository, FundTransactionRepository fundTransactionRepository) {
        this.fundRepository = fundRepository;
        this.fundTransactionRepository = fundTransactionRepository;
    }

    @Transactional
    /*
     * Seeds a newly created fund with the initial balance of $10,000,000 and records it as a DEPOSIT transaction.
     * Called once during user registration, immediately after the user and fund are persisted.
     * Params:
     * - fund: The newly created FundEntity to initialize.
     * - user: The user who owns the fund (used for transaction attribution).
     * Returns: void.
     * */
    public void recordInitialBalance(FundEntity fund, UserEntity user) {
        fund.setTotalCapital(INITIAL_BALANCE);
        fundRepository.save(fund);

        fundTransactionRepository.save(FundTransactionEntity.builder()
                .amount(INITIAL_BALANCE)
                .user(user)
                .type(FundTransactionType.DEPOSIT)
                .fund(fund)
                .notes("Initial Balance")
                .build());
    }

    @Transactional
    /*
     * Adds capital to a fund by increasing its totalCapital and recording a DEPOSIT transaction.
     * Params:
     * - fundId: The ID of the fund to credit.
     * - amount: The amount of capital to add.
     * - user: The user performing the deposit (used for transaction attribution).
     * Returns: void.
     * */
    public void addToFund(Long fundId, BigDecimal amount, UserEntity user) {
        FundEntity fund = fundRepository.findById(fundId).orElseThrow();
        fund.setTotalCapital(fund.getTotalCapital().add(amount));
        fundRepository.save(fund);

        fundTransactionRepository.save(FundTransactionEntity.builder()
                .amount(amount)
                .user(user)
                .type(FundTransactionType.DEPOSIT)
                .fund(fund)
                .notes("Capital Deposit")
                .build());
    }

    @Transactional
    /*
     * Withdraws capital from a fund by decreasing its totalCapital and recording a WITHDRAWAL transaction.
     * Validation:
     * - The requested amount must not exceed available dry powder (uninvested capital).
     * Params:
     * - fundId: The ID of the fund to debit.
     * - amount: The amount of capital to withdraw.
     * - user: The user performing the withdrawal (used for transaction attribution).
     * Returns: void. Throws IllegalArgumentException if insufficient dry powder.
     * */
    public void withdraw(Long fundId, BigDecimal amount, UserEntity user) {
        FundEntity fund = fundRepository.findById(fundId).orElseThrow();
        if (amount.compareTo(fund.getDryPowder()) > 0) {
            throw new IllegalArgumentException(
                    "Cannot withdraw $" + amount + ". Available dry powder: $" + fund.getDryPowder());
        }
        fund.setTotalCapital(fund.getTotalCapital().subtract(amount));
        fundRepository.save(fund);

        fundTransactionRepository.save(FundTransactionEntity.builder()
                .amount(amount)
                .user(user)
                .type(FundTransactionType.WITHDRAWAL)
                .fund(fund)
                .notes("Capital withdrawal")
                .build());
    }

    @Transactional
    /*
     * Records a permanent capital loss (write-off) against the fund by reducing totalCapital
     * and logging a WRITE_OFF transaction. Called when an investment is marked as WRITTEN_OFF.
     * Params:
     * - fund: The FundEntity to write off against.
     * - amount: The amount being written off.
     * - user: The user who owns the fund (used for transaction attribution).
     * - notes: A description of the write-off reason.
     * Returns: void.
     * */
    public void recordFundWriteOff(FundEntity fund, BigDecimal amount, UserEntity user, String notes) {
        fund.setTotalCapital(fund.getTotalCapital().subtract(amount));
        fundRepository.save(fund);

        fundTransactionRepository.save(FundTransactionEntity.builder()
                .amount(amount)
                .user(user)
                .type(FundTransactionType.WRITE_OFF)
                .fund(fund)
                .notes(notes)
                .build());
    }

    @Transactional
    /*
     * Credits the fund with returned capital (e.g., from an exited investment) and records a DEPOSIT transaction.
     * Params:
     * - fund: The FundEntity to credit.
     * - amount: The amount to add back to the fund.
     * - user: The user who owns the fund (used for transaction attribution).
     * - notes: A description of the deposit reason.
     * Returns: void.
     * */
    public void recordFundDeposit(FundEntity fund, BigDecimal amount, UserEntity user, String notes) {
        fund.setTotalCapital(fund.getTotalCapital().add(amount));
        fundRepository.save(fund);

        fundTransactionRepository.save(FundTransactionEntity.builder()
                .amount(amount)
                .user(user)
                .type(FundTransactionType.DEPOSIT)
                .fund(fund)
                .notes(notes)
                .build());
    }
}
