package dev.saberlabs.myspringportfolio.user;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Mirror the exact construction pattern used by AuthService.registerUser()
        FundEntity fund = new FundEntity();
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setFund(fund);

        UserEntity user = new UserEntity();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("hashed_password");
        user.setPortfolio(portfolio);

        // persistAndFlush writes to H2; clear() removes L1 cache to force real DB queries
        em.persistAndFlush(user);
        em.clear();
    }

    // ── findByUsername ────────────────────────────────────────────────────────

    @Test
    void findByUsername_returnsUser_whenUsernameExists() {
        Optional<UserEntity> result = userRepository.findByUsername("alice");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findByUsername_returnsEmpty_whenUsernameNotFound() {
        Optional<UserEntity> result = userRepository.findByUsername("nobody");

        assertThat(result).isEmpty();
    }

    // ── findByEmail ───────────────────────────────────────────────────────────

    @Test
    void findByEmail_returnsUser_whenEmailExists() {
        Optional<UserEntity> result = userRepository.findByEmail("alice@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void findByEmail_returnsEmpty_whenEmailNotFound() {
        Optional<UserEntity> result = userRepository.findByEmail("nobody@example.com");

        assertThat(result).isEmpty();
    }

    // ── findByUsernameOrEmail ─────────────────────────────────────────────────

    @Test
    void findByUsernameOrEmail_returnsUser_whenMatchedByUsername() {
        Optional<UserEntity> result = userRepository.findByUsernameOrEmail("alice", "other@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("alice");
    }

    @Test
    void findByUsernameOrEmail_returnsUser_whenMatchedByEmail() {
        Optional<UserEntity> result = userRepository.findByUsernameOrEmail("other", "alice@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void findByUsernameOrEmail_returnsEmpty_whenNeitherMatches() {
        Optional<UserEntity> result = userRepository.findByUsernameOrEmail("nobody", "nobody@example.com");

        assertThat(result).isEmpty();
    }
}
