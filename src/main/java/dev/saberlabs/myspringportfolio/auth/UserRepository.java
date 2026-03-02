package dev.saberlabs.myspringportfolio.auth;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
    Optional<UserEntity> findByEmail(String email);
}
