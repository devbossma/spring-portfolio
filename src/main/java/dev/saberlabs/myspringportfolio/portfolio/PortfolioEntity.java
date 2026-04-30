package dev.saberlabs.myspringportfolio.portfolio;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="portfolios")
/*
 * JPA entity representing a user's investment portfolio.
 * Acts as the central aggregation point for a user's investments and fund.
 * Tracks the total amount invested across all active investments and the portfolio's status.
 * */
public class PortfolioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserEntity user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvestmentEntity> investments =  new ArrayList<>();


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn( name = "fund_id", referencedColumnName = "id", nullable = false)
    private FundEntity fund;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalInvested = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private PortfolioStatus status = PortfolioStatus.ACTIVE;


    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;




    /*
     * Associates an InvestmentEntity with this portfolio, ensuring the bidirectional relationship is maintained.
     * Params:
     * - investment: The investment to add. No-op if null.
     * Returns: void.
     * */
    public void addInvestment(InvestmentEntity investment) {
        if (investment != null) {
            if (investments == null) {
                investments = new ArrayList<>();
            }
            investments.add(investment);
            investment.setPortfolio(this);
        }
    }

        /*
         * Removes an InvestmentEntity from this portfolio and clears the back-reference on the investment.
         * Params:
         * - investment: The investment to remove. No-op if null or not in the list.
         * Returns: void.
         * */
        public void removeInvestment(InvestmentEntity investment) {
            if (investment != null && investments != null) {
                investments.remove(investment);
                investment.setPortfolio(null);
            }
        }
}
