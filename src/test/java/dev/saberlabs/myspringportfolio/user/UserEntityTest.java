package dev.saberlabs.myspringportfolio.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void getAuthorities_returnsRoleUser_whenRoleIsUser() {
        UserEntity user = new UserEntity();
        user.setUsername("yassine");
        user.setEmail("yassine@example.com");
        user.setPassword("hashed_password");
        // role defaults to Role.USER via field initializer
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // The getAuthorities() method should return a collection with a single authority "ROLE_USER"
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void getAuthorities_returnsRoleAdmin_whenRoleIsAdmin() {
        UserEntity user = new UserEntity();
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setPassword("hashed_password");
        user.setRole(Role.ADMIN);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // The getAuthorities() method should return a collection with a single authority "ROLE_ADMIN"
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void newUser_hasDefaultRoleUser() {
        UserEntity user = new UserEntity();

        // By default, the role should be Role.USER due to the field initializer in UserEntity
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void newUser_hasAllAccountFlagsTrue() {
        UserEntity user = new UserEntity();

        // By default, all account status flags should be true for a new user
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
    }
}
