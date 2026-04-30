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
/*
 * JPA entity representing a single fund-level financial transaction.
 * Extends TransactionEntity for shared fields (id, amount, user, createdAt, notes).
 * Each record captures a DEPOSIT, WITHDRAWAL, or WRITE_OFF operation against a fund.
 * The validated setter ensures only recognized FundTransactionType values are persisted.
 * */
public class FundTransactionEntity extends TransactionEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private FundTransactionType type;

    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false)
    private FundEntity fund;


    /*
     * Sets the transaction type after validating it is a recognized FundTransactionType value.
     * Params:
     * - type: The FundTransactionType to assign. Throws IllegalArgumentException if null or invalid.
     * Returns: void.
     * */
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


    /*
     * Validates that a WITHDRAWAL transaction does not exceed the available fund balance.
     * Only runs the check if the transaction type is WITHDRAWAL.
     * Params:
     * - fundBalance: The current available balance in the fund.
     * Returns: void. Throws IllegalStateException if the withdrawal amount exceeds the balance.
     * */
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

    /*
     * Returns the category label for this transaction type, used for display and ledger reporting.
     * Returns: "FUND".
     * */
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