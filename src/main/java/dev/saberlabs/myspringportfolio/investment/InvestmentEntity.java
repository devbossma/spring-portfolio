package dev.saberlabs.myspringportfolio.investment;


import dev.saberlabs.myspringportfolio.sector.SectorEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="investments")
public class InvestmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Long portfolioId;

    @OneToOne
    @JoinColumn(name = "sector_id")
    private SectorEntity sector;

    @Column(name = "status")
    private InvestmentStatus status = InvestmentStatus.PENDING;

    private String name;
    private Double initialValue;
    private Double currentValue;
    private Integer quantity;
    private Double investedAmount;
    private Double profitLoss;
    private Double exitValue;


    // Timestamps
    @CreationTimestamp
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;
    private LocalDate exitDate;

}
