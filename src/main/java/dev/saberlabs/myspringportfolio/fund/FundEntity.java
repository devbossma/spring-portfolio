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
