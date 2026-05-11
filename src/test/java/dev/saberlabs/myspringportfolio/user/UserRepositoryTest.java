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
        // Mirroring the exact construction pattern used by AuthService.registerUser()
        FundEntity fund = new FundEntity();
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setFund(fund);

        UserEntity user = new UserEntity();
        user.setUsername("yassine");
        user.setEmail("yassine@example.com");
        user.setPassword("hashed_password");
        user.setPortfolio(portfolio);

        // persistAndFlush writes to H2; clear() removes L1 cache to force real DB queries
        em.persistAndFlush(user);
        em.clear();
    }


    // testing findByUsername method is straightforward since it should return an Optional<UserEntity> based on the username.
    // We will test both the case where the username exists and where it does not exist.

    // findByUsername
    @Test
    void findByUsername_returnsUser_whenUsernameExists() {
        Optional<UserEntity> result = userRepository.findByUsername("yassine");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("yassine");
    }

    // findByUsername should return an empty Optional when the username is not found in the database. We will test this by querying for a username that we know does not exist.
    @Test
    void findByUsername_returnsEmpty_whenUsernameNotFound() {
        Optional<UserEntity> result = userRepository.findByUsername("nobody");

        assertThat(result).isEmpty();
    }

    // findByEmail

    @Test
    void findByEmail_returnsUser_whenEmailExists() {
        Optional<UserEntity> result = userRepository.findByEmail("yassine@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("yassine@example.com");
    }

    @Test
    void findByEmail_returnsEmpty_whenEmailNotFound() {
        Optional<UserEntity> result = userRepository.findByEmail("nobody@example.com");

        assertThat(result).isEmpty();
    }

    // findByUsernameOrEmail

    @Test
    void findByUsernameOrEmail_returnsUser_whenMatchedByUsername() {
        Optional<UserEntity> result = userRepository.findByUsernameOrEmail("yassine", "other@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("yassine");
    }

    @Test
    void findByUsernameOrEmail_returnsUser_whenMatchedByEmail() {
        Optional<UserEntity> result = userRepository.findByUsernameOrEmail("other", "yassine@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("yassine@example.com");
    }

    @Test
    void findByUsernameOrEmail_returnsEmpty_whenNeitherMatches() {
        Optional<UserEntity> result = userRepository.findByUsernameOrEmail("nobody", "nobody@example.com");

        assertThat(result).isEmpty();
    }
}
