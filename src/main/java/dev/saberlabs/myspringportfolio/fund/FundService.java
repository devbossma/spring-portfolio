package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.transaction.FundTransactionEntity;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionRepository;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionType;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FundService {

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(10_000_000L);

    private final FundRepository fundRepository;
    private final FundTransactionRepository fundTransactionRepository;

    public FundService(FundRepository fundRepository, FundTransactionRepository fundTransactionRepository) {
        this.fundRepository = fundRepository;
        this.fundTransactionRepository = fundTransactionRepository;
    }

    @Transactional
    public void recordInitialBalance(FundEntity fund, UserEntity user) {
        fund.setTotalCapital(INITIAL_BALANCE);
        fundRepository.save(fund);

        fundTransactionRepository.save(FundTransactionEntity.builder()
                .amount(INITIAL_BALANCE)
                .user(user)
                .type(FundTransactionType.DEPOSIT)
                .fund(fund)
                .notes("Initial balance")
                .build());
    }

    @Transactional
    public void addToFund(Long fundId, BigDecimal amount, UserEntity user) {
        FundEntity fund = fundRepository.findById(fundId).orElseThrow();
        fund.setTotalCapital(fund.getTotalCapital().add(amount));
        fundRepository.save(fund);

        fundTransactionRepository.save(FundTransactionEntity.builder()
                .amount(amount)
                .user(user)
                .type(FundTransactionType.DEPOSIT)
                .fund(fund)
                .notes("Capital deposit")
                .build());
    }

    @Transactional
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
