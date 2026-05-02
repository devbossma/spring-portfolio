package dev.saberlabs.myspringportfolio.transaction;


import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import dev.saberlabs.myspringportfolio.investment.InvestmentType;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@Entity
@Table(name = "investment_transactions")
@NoArgsConstructor
/*
 * JPA entity representing a single investment-level financial transaction.
 * Extends TransactionEntity for shared fields (id, amount, user, createdAt, notes).
 * Each record captures a BUY, SELL, or WRITE_OFF operation against a specific investment.
 * Tracks price-per-unit and quantity to support fractional/unit-based investment types.
 * The validated setter ensures only recognized InvestmentTransactionType values are persisted.
 * */
public class InvestmentTransactionEntity extends TransactionEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private InvestmentTransactionType type;

    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = true)
    private InvestmentEntity investment;

    @Column(precision = 19, scale = 2)
    private BigDecimal pricePerUnit;

    private Integer quantity;

    /*
     * Sets the transaction type after validating it is a recognized InvestmentTransactionType value.
     * Params:
     * - type: The InvestmentTransactionType to assign. Throws IllegalArgumentException if null or invalid.
     * Returns: void.
     * */
    public void setType(InvestmentTransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "Investment transaction type cannot be null"
            );
        }
        if (!InvestmentTransactionType.isValid(type)) {
            throw new IllegalArgumentException(
                    "Invalid investment transaction type: " + type +
                            ". Allowed: " + Arrays.toString(InvestmentTransactionType.values())
            );
        }
        this.type = type;
    }


    /*
     * Validates that a SELL transaction does not exceed the currently held quantity.
     * Only runs the check if the transaction type is SELL.
     * Params:
     * - currentQuantity: The number of units currently held in the investment.
     * Returns: void. Throws IllegalStateException if the sell quantity exceeds the held quantity.
     * */
    public void validateSell(Integer currentQuantity) {
        if (this.type == InvestmentTransactionType.SELL) {
            if (this.quantity > currentQuantity) {
                throw new IllegalStateException(
                        "Cannot sell " + this.quantity +
                                " units. Only " + currentQuantity + " held."
                );
            }
        }
    }

    /*
     * Calculates the total monetary value of this transaction (pricePerUnit × quantity).
     * Returns BigDecimal.ZERO if either pricePerUnit or quantity is null.
     * Returns: The total transaction value as a BigDecimal.
     * */
    public BigDecimal getTotalValue() {
        if (pricePerUnit == null || quantity == null) return BigDecimal.ZERO;
        return pricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }

    /*
     * Returns the category label for this transaction type, used for display and ledger reporting.
     * Returns: "INVESTMENT".
     * */
    @Override
    public String getTransactionCategory() {
        return "INVESTMENT";
    }

    @Builder
    public InvestmentTransactionEntity(String id, BigDecimal amount,
                                       UserEntity user, String notes,
                                       InvestmentTransactionType type,
                                       InvestmentEntity investment,
                                       BigDecimal pricePerUnit,
                                       Integer quantity) {
        super();
        this.setAmount(amount);
        this.setUser(user);
        this.setNotes(notes);
        this.setType(type);   // ← goes through validated setter
        this.investment = investment;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
    }
}
