package dev.saberlabs.myspringportfolio.user;

import dev.saberlabs.myspringportfolio.investment.InvestmentEntity;
import dev.saberlabs.myspringportfolio.notification.NotificationEntity;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.transaction.TransactionEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.net.ssl.SSLSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String username;

    @NonNull
    @Column(unique = true, nullable = false)
    private String email;

    @NonNull
    private String password;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "portfolio_id", referencedColumnName = "id", nullable = false)
    private PortfolioEntity portfolio;


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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationEntity> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionEntity> transactions;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
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
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;



}
