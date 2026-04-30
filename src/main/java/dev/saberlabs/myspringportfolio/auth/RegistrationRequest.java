package dev.saberlabs.myspringportfolio.auth;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
/*
 * A simple form-binding model that holds user input from the registration form.
 * Used as a @ModelAttribute in AuthController to bind HTML form fields to Java fields.
 * */
public class RegistrationRequest {
    private String username;
    private String email;
    private String password;
    private String confirmpassword;
}
