package dev.saberlabs.myspringportfolio.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundTransactionRepository extends JpaRepository<FundTransactionEntity, String> {

    List<FundTransactionEntity> findByFundIdOrderByCreatedAtDesc(Long fundId);
    List<FundTransactionEntity> findTop3ByFundIdOrderByCreatedAtDesc(Long fundId);
}
