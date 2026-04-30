package dev.saberlabs.myspringportfolio.auth;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
/*
 * Handles HTTP requests for authentication flows: login, registration, and registration form submission.
 * Redirects already-authenticated users away from auth pages to prevent re-login.
 * */
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    /*
     * Serves the login page.
     * Params:
     * - authentication: The current Spring Security authentication context.
     * Returns: The login view, or a redirect to /portfolio if the user is already authenticated.
     * */
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/portfolio";
        }
        return "auth/login";
    }

    @GetMapping("/register")
    /*
     * Serves the registration page with an empty RegistrationRequest form model.
     * Params:
     * - authentication: The current Spring Security authentication context.
     * - model: The MVC model used to pass the empty registration form to the view.
     * Returns: The registration view, or a redirect to /portfolio if the user is already authenticated.
     * */
    public String register(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/portfolio";
        }
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    /*
     * Handles registration form submission.
     * On success, redirects to the login page with a flash success message.
     * On failure (e.g. duplicate username/email or password mismatch), re-renders the form with an error message.
     * Params:
     * - request: The populated RegistrationRequest form data.
     * - model: The MVC model for passing error data back to the view.
     * - redirectAttributes: Flash attributes used to pass a success message on redirect.
     * Returns: A redirect to /auth/login on success, or the register view on error.
     * */
    public String handleRegister(
            @ModelAttribute("registrationRequest") RegistrationRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            authService.registerUser(request);
            redirectAttributes.addFlashAttribute("successMessage", "Account created! Please sign in.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("registrationRequest", request);
            return "auth/register";
        }
    }
}
