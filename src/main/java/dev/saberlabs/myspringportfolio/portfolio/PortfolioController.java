package dev.saberlabs.myspringportfolio.portfolio;


import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/portfolio")
    public String portfolio(@AuthenticationPrincipal UserEntity currentUser, Model model) {
        var portfolio = currentUser.getPortfolio();
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("fund", portfolio.getFund());
        model.addAttribute("investments", portfolio.getInvestments());
        return "portfolio";
    }



}
