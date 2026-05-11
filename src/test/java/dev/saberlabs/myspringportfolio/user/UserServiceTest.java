package dev.saberlabs.myspringportfolio.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void loadUserByUsername_returnsUserDetails_whenUserExists() {
        UserEntity user = new UserEntity();
        user.setUsername("yassine");
        user.setEmail("yassine@example.com");
        user.setPassword("hashed_password");

        when(userRepository.findByUsername("yassine")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("yassine");

        assertThat(result.getUsername()).isEqualTo("yassine");
        assertThat(result.getPassword()).isEqualTo("hashed_password");
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("ghost");
    }
}
