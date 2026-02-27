package dev.saberlabs.myspringportfolio.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String name;
    private String description;
    private Double amount;
    private String riskLevel;
    @NonNull
    private String sector;
    @NonNull
    private String status;
    private Double expectedReturn;
    private Double actualReturn;


    // Timestamps for tracking creation and updates
    @CreationTimestamp
    private LocalDate startDate;
    @UpdateTimestamp
    private LocalDate updatedAt;
    private LocalDate endDate;



}
