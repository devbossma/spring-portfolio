package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.transaction.InvestmentTransactionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@ToString(exclude = {"portfolio", "transactions"}) // avoid lazy-load traps
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name="investments")
public class InvestmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioEntity portfolio;


    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private InvestmentType type;

    @Column(name = "sector")
    @Enumerated(EnumType.STRING)
    private InvestmentSector sector;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InvestmentStatus status = InvestmentStatus.PENDING;

    @Column(name = "stage")
    @Enumerated(EnumType.STRING)
    private InvestmentStage stage;

    @Column(name = "risk_level")
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;


    @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvestmentTransactionEntity> transactions = new ArrayList<>();


    @NonNull
    private String name;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal pricePerUnit; // Price at which the investment was made

    @Column( precision = 19, scale = 2, nullable = false)
    private BigDecimal currentValue; // Current price per unit

    private Integer quantity = 1;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal investedAmount; // Total amount invested (pricePerUnit * quantity)

    @Column( precision = 19, scale = 2)
    private BigDecimal exitValue; //

    // Timestamps
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime exitAt;

    @Transient
    public BigDecimal getProfitLoss() {
        if (currentValue == null || pricePerUnit == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return currentValue.subtract(pricePerUnit)
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Transient
    public BigDecimal getCurrentTotalValue() {
        if (currentValue == null || quantity == null) return BigDecimal.ZERO;
        return currentValue.multiply(BigDecimal.valueOf(quantity));
    }

    @Transient
    public BigDecimal getProfitLossPercentage() {
        if (pricePerUnit == null || pricePerUnit.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentValue.subtract(pricePerUnit)
                .divide(pricePerUnit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isActive()   { return this.status == InvestmentStatus.ACTIVE; }
    public boolean isExited()   { return this.status == InvestmentStatus.EXITED; }
    public boolean isPending()  { return this.status == InvestmentStatus.PENDING; }
    public boolean isFailed()   { return this.status == InvestmentStatus.WRITTEN_OFF; }
}
