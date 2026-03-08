package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Double balance = 10_000_000.00;
    private Double investedAmount = 0.00;
    private Double profitLoss = 0.00;

    @OneToOne(mappedBy = "fund", cascade = CascadeType.ALL, orphanRemoval = true)
    private PortfolioEntity portfolio;
}
