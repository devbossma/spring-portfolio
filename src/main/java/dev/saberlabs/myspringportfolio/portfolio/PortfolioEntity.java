package dev.saberlabs.myspringportfolio.portfolio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

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

    @OneToOne(mappedBy = "user")
    @NonNull
    private Long userId;

    @OneToMany(mappedBy = "investment")
    private List<Long> investments;

    @OneToOne(mappedBy = "fund")
    @NonNull
    private Long fundId;

}
