package dev.saberlabs.myspringportfolio.portfolio;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/*
 * Spring Data repository for PortfolioEntity.
 * Provides standard CRUD operations. Portfolio lookup is done by portfolio ID,
 * which is always available through the authenticated user's UserEntity.
 * */
public interface PortfolioRepository extends CrudRepository<PortfolioEntity, Long> {


}
