package dev.saberlabs.myspringportfolio.user;

import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
/*
 * Implements Spring Security's UserDetailsService to load user details during authentication.
 * Retrieves a UserEntity by username from the database, which Spring Security uses to verify
 * credentials and build the security context.
 * */
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @NonNull
    /*
     * Loads a user by username for Spring Security authentication.
     * Params:
     * - username: The username submitted on the login form.
     * Returns: A UserDetails instance (UserEntity). Throws UsernameNotFoundException if no user is found.
     * */
    public UserDetails loadUserByUsername( @NonNull  String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
