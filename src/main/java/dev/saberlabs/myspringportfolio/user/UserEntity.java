package dev.saberlabs.myspringportfolio.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;

    @Column(name = "active")
    private boolean isActive = true;
    @Column(name= "enabled")
    private boolean isEnabled = true;
    @Column(name= "credentials_non_expired")
    private boolean isCredentialsNonExpired = true;
    @Column(name= "locked")
    private boolean isAccountNonLocked = true;
    @Column(name= "account_non_expired")
    private boolean isAccountNonExpired = true;
    private String role; // "USER", "ADMIN"

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_" + this.role);
        return List.of(userAuthority);
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    // Timestamps for tracking creation and updates
    @CreationTimestamp
    private LocalDate createdAt;
    @UpdateTimestamp
    private LocalDate updatedAt;
}
