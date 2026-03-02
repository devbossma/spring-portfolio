package dev.saberlabs.myspringportfolio.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "/auth/login";
    }

    // Spring Security will handle the authentication process, so no PostMapping needed for User's authentication.

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "/auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(
            @ModelAttribute("registrationRequest") RegistrationRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        this.authService.registerUser( request);
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Registration successful! Please log in."
        );
        return "redirect:/auth/login";
    }

    @GetMapping("logout")
    public String logout() {
        return "redirect:/auth/login?logout";
    }
}
