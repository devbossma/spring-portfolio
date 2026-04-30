package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.transaction.FundTransactionEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="funds")
@Builder
/*
 * JPA entity representing a user's investment fund.
 * Tracks total capital, deployed capital (amount currently invested), and profit/loss.
 * Linked one-to-one with a PortfolioEntity and one-to-many with FundTransactionEntity records.
 * */
public class FundEntity {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(precision = 19, scale = 2)
    private BigDecimal totalCapital = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal deployedCapital = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal profitLoss = BigDecimal.ZERO;

    @Transient
    /*
     * Calculates the uninvested (available) capital in the fund, known as "dry powder".
     * This is a derived, non-persisted value computed as totalCapital minus deployedCapital.
     * Returns: The amount of capital available for new investments.
     * */
    public BigDecimal getDryPowder() {
        return totalCapital.subtract(deployedCapital);
    }

    @OneToOne(mappedBy = "fund", cascade = CascadeType.ALL, orphanRemoval = true)
    private PortfolioEntity portfolio;

    @OneToMany(mappedBy = "fund", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FundTransactionEntity> transactions = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
