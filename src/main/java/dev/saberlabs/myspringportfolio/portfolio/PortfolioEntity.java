package dev.saberlabs.myspringportfolio.portfolio;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="portfolios")
public class PortfolioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserEntity user;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvestmentEntity> investments =  new ArrayList<>();


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn( name = "fund_id", referencedColumnName = "id", nullable = false)
    private FundEntity fund;




    public void addInvestment(InvestmentEntity investment) {
        if (investment != null) {
            if (investments == null) {
                investments = new ArrayList<>();
            }
            investments.add(investment);
            investment.setPortfolio(this);
        }
    }

        public void removeInvestment(InvestmentEntity investment) {
            if (investment != null && investments != null) {
                investments.remove(investment);
                investment.setPortfolio(null);
            }
        }
}
