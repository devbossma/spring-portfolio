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
public class InvestmentTransactionEntity extends TransactionEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private InvestmentTransactionType type;

    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = false)
    private InvestmentEntity investment;

    @Column(precision = 19, scale = 2)
    private BigDecimal pricePerUnit;

    private Integer quantity;

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

    public BigDecimal getTotalValue() {
        if (pricePerUnit == null || quantity == null) return BigDecimal.ZERO;
        return pricePerUnit.multiply(BigDecimal.valueOf(quantity));
    }

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
