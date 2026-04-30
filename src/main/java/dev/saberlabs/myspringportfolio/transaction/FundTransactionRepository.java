package dev.saberlabs.myspringportfolio.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/*
 * Spring Data JPA repository for FundTransactionEntity.
 * Provides standard CRUD and custom queries for retrieving fund transaction history.
 * - findByFundIdOrderByCreatedAtDesc: Full chronological history for the transaction ledger view.
 * - findTop3ByFundIdOrderByCreatedAtDesc: The 3 most recent transactions for the portfolio dashboard.
 * */
public interface FundTransactionRepository extends JpaRepository<FundTransactionEntity, String> {

    List<FundTransactionEntity> findByFundIdOrderByCreatedAtDesc(Long fundId);
    List<FundTransactionEntity> findTop3ByFundIdOrderByCreatedAtDesc(Long fundId);
}
