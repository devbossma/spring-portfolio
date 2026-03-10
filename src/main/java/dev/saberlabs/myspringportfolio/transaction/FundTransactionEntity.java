package dev.saberlabs.myspringportfolio.transaction;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@Entity
@Table(name = "fund_transactions")
@NoArgsConstructor
public class FundTransactionEntity extends TransactionEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private FundTransactionType type;

    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false)
    private FundEntity fund;


    public void setType(FundTransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "Fund transaction type cannot be null"
            );
        }
        // Guard: only allow fund-level types
        if (!FundTransactionType.isValid(type)) {
            throw new IllegalArgumentException(
                    "Invalid fund transaction type: " + type +
                            ". Allowed: " + Arrays.toString(FundTransactionType.values())
            );
        }
        this.type = type;
    }


    public void validateWithdrawal(BigDecimal fundBalance) {
        if (this.type == FundTransactionType.WITHDRAWAL) {
            if (getAmount().compareTo(fundBalance) > 0) {
                throw new IllegalStateException(
                        "Withdrawal amount " + getAmount() +
                                " exceeds available fund balance " + fundBalance
                );
            }
        }
    }

    @Override
    public String getTransactionCategory() {
        return "FUND";
    }

    @Builder
    public FundTransactionEntity(String id, BigDecimal amount,
                                 UserEntity user, String notes,
                                 FundTransactionType type, FundEntity fund) {
        super(); // calls parent no-args
        this.setAmount(amount);
        this.setUser(user);
        this.setNotes(notes);
        this.setType(type);   // ← goes through validated setter
        this.fund = fund;
    }
}