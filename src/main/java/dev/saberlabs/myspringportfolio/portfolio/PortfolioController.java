package dev.saberlabs.myspringportfolio.portfolio;

import dev.saberlabs.myspringportfolio.transaction.FundTransactionRepository;
import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final FundTransactionRepository fundTransactionRepository;

    public PortfolioController(PortfolioService portfolioService,
                               FundTransactionRepository fundTransactionRepository) {
        this.portfolioService = portfolioService;
        this.fundTransactionRepository = fundTransactionRepository;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/portfolio";
        }
        return "home";
    }

    @GetMapping("/portfolio")
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
