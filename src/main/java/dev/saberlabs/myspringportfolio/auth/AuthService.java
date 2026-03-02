package dev.saberlabs.myspringportfolio.auth;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AuthService {



    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserEntity registerUser(RegistrationRequest request) {
        String email =  Objects.requireNonNull(request.getEmail());
        String username = Objects.requireNonNull(request.getUsername());
        String password = Objects.requireNonNull(request.getPassword());
        String confirmPassword = Objects.requireNonNull(request.getConfirmpassword());
        if (this.userRepository.findByUsernameOrEmail(email, username).isPresent()) {
            throw new IllegalArgumentException("Username or email already exists");
        }

        if (!password.equals(confirmPassword)){
            throw new IllegalArgumentException("Passwords do not match");
        }

        // ... validation checks
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(Objects.requireNonNull(passwordEncoder.encode(password)));
        userRepository.save(user);
        return user;
    }
}
