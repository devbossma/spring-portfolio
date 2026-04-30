package dev.saberlabs.myspringportfolio.transaction;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "transactions")
/*
 * Abstract base entity for all transaction types in the system.
 * Uses JPA JOINED inheritance — each subtype (FundTransactionEntity, InvestmentTransactionEntity)
 * has its own table that joins to this base "transactions" table via the shared UUID primary key.
 * Stores common fields: UUID id, amount, owning user, timestamp, and notes.
 * Subclasses must implement getTransactionCategory() to identify their domain.
 * */
public abstract class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String notes;

    public abstract String getTransactionCategory();
}
