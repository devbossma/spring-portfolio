package dev.saberlabs.myspringportfolio.investment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends CrudRepository<InvestmentEntity, Long> {
}
