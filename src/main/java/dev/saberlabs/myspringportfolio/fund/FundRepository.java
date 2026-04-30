package dev.saberlabs.myspringportfolio.fund;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Spring Data JPA repository for FundEntity.
 * Provides standard CRUD operations. No custom queries are needed as fund access
 * is always done through the associated portfolio or by fund ID.
 * */
public interface FundRepository extends JpaRepository<FundEntity, Long> { }
