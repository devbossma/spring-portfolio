package dev.saberlabs.myspringportfolio.auth;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String confirmpassword;
}
