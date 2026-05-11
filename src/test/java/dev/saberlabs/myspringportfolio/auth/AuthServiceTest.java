package dev.saberlabs.myspringportfolio.auth;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundService;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import dev.saberlabs.myspringportfolio.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private FundService fundService;

    @InjectMocks
    private AuthService authService;

    private RegistrationRequest request(String email, String username, String password, String confirm) {
        RegistrationRequest req = new RegistrationRequest();
        req.setEmail(email);
        req.setUsername(username);
        req.setPassword(password);
        req.setConfirmpassword(confirm);
        return req;
    }

    //  registerUser 

    @Test
    void registerUser_savesEncodedUserWithPortfolioAndFund_whenValid() {
        when(userRepository.findByUsernameOrEmail("yassine@example.com", "Yassine"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("hashed");

        authService.registerUser(request("yassine@example.com", "Yassine", "secret", "secret"));

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();

        assertThat(saved.getEmail()).isEqualTo("yassine@example.com");
        assertThat(saved.getUsername()).isEqualTo("Yassine");
        assertThat(saved.getPassword()).isEqualTo("hashed");
        assertThat(saved.getPortfolio()).isNotNull();
        assertThat(saved.getPortfolio().getFund()).isNotNull();

        verify(fundService).recordInitialBalance(any(FundEntity.class), eq(saved));
    }

    @Test
    void registerUser_throwsIllegalArgument_whenUsernameOrEmailAlreadyExists() {
        when(userRepository.findByUsernameOrEmail("yassine@example.com", "Yassine"))
                .thenReturn(Optional.of(new UserEntity()));

        assertThatThrownBy(() ->
                authService.registerUser(request("yassine@example.com", "Yassine", "secret", "secret")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void registerUser_throwsIllegalArgument_whenPasswordsDoNotMatch() {
        when(userRepository.findByUsernameOrEmail("yassine@example.com", "Yassine"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.registerUser(request("yassine@example.com", "Yassine", "secret", "wrong")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passwords do not match");
    }
}
