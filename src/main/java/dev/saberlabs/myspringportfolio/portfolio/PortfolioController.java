package dev.saberlabs.myspringportfolio.portfolio;

import dev.saberlabs.myspringportfolio.transaction.FundTransactionRepository;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
/*
 * Handles HTTP GET requests for the application's home and portfolio views.
 * Resolves the authenticated user's portfolio data and passes it to the Thymeleaf templates.
 * */
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final FundTransactionRepository fundTransactionRepository;

    public PortfolioController(PortfolioService portfolioService,
                               FundTransactionRepository fundTransactionRepository) {
        this.portfolioService = portfolioService;
        this.fundTransactionRepository = fundTransactionRepository;
    }

    @GetMapping("/")
    /*
     * Serves the application's home (landing) page.
     * Redirects authenticated users directly to their portfolio.
     * Params:
     * - authentication: The current Spring Security authentication context.
     * Returns: The "home" view, or a redirect to /portfolio if already authenticated.
     * */
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/portfolio";
        }
        return "home";
    }

    @GetMapping("/portfolio")
    /*
     * Serves the main portfolio view with all relevant data for the authenticated user.
     * Adds the portfolio, fund, investment list, and the 3 most recent fund transactions to the model.
     * Params:
     * - currentUser: The authenticated user whose portfolio data will be displayed.
     * - model: The MVC model used to pass data to the portfolio Thymeleaf template.
     * Returns: The "portfolio" view.
     * */
    public String portfolio(@AuthenticationPrincipal UserEntity currentUser, Model model) {
        var portfolio = portfolioService.getPortfolioByUser(currentUser);
        Long fundId = portfolio.getFund().getId();

        model.addAttribute("portfolio", portfolio);
        model.addAttribute("fund", portfolio.getFund());
        model.addAttribute("investments", portfolio.getInvestments());
        model.addAttribute("recentFundTransactions",
                fundTransactionRepository.findTop3ByFundIdOrderByCreatedAtDesc(fundId));
        return "portfolio";
    }
}
