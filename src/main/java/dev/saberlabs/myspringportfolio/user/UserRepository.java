package dev.saberlabs.myspringportfolio.user;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
/*
 * Spring Data CRUD repository for UserEntity.
 * Provides standard CRUD operations and custom lookup methods used during authentication and registration.
 * */
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /*
     * Looks up a user by their username. Used by UserService.loadUserByUsername during Spring Security authentication.
     * Params:
     * - username: The username to search for.
     * Returns: An Optional containing the UserEntity if found, or empty if not.
     * */
    Optional<UserEntity> findByUsername(String username);

    /*
     * Looks up a user by either username or email. Used during registration to enforce uniqueness.
     * Params:
     * - username: The username to check.
     * - email: The email address to check.
     * Returns: An Optional containing the UserEntity if a match is found on either field, or empty if not.
     * */
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    /*
     * Looks up a user by their email address.
     * Params:
     * - email: The email to search for.
     * Returns: An Optional containing the UserEntity if found, or empty if not.
     * */
    Optional<UserEntity> findByEmail(String email);
}
