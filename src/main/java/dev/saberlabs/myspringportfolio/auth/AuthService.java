package dev.saberlabs.myspringportfolio.auth;

import dev.saberlabs.myspringportfolio.fund.FundEntity;
import dev.saberlabs.myspringportfolio.fund.FundService;
import dev.saberlabs.myspringportfolio.portfolio.PortfolioEntity;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import dev.saberlabs.myspringportfolio.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FundService fundService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, FundService fundService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fundService = fundService;
    }

    @Transactional
    public void registerUser(RegistrationRequest request) {
        String email           = Objects.requireNonNull(request.getEmail());
        String username        = Objects.requireNonNull(request.getUsername());
        String password        = Objects.requireNonNull(request.getPassword());
        String confirmPassword = Objects.requireNonNull(request.getConfirmpassword());

        if (userRepository.findByUsernameOrEmail(email, username).isPresent()) {
            throw new IllegalArgumentException("Username or email already exists");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        FundEntity fund = new FundEntity();
        PortfolioEntity portfolio = new PortfolioEntity();
        portfolio.setFund(fund);
        user.setPortfolio(portfolio);

        userRepository.save(user); // persists user → portfolio → fund via cascade

        // Record the initial $10M balance as the first fund transaction
        fundService.recordInitialBalance(fund, user);
    }
}
