package dev.saberlabs.myspringportfolio.transaction;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final FundTransactionRepository fundTransactionRepository;
    private final InvestmentTransactionRepository investmentTransactionRepository;

    public TransactionController(FundTransactionRepository fundTransactionRepository,
                                 InvestmentTransactionRepository investmentTransactionRepository) {
        this.fundTransactionRepository = fundTransactionRepository;
        this.investmentTransactionRepository = investmentTransactionRepository;
    }

    @GetMapping("")
    public String list(@AuthenticationPrincipal UserEntity currentUser, Model model) {
        Long fundId      = currentUser.getPortfolio().getFund().getId();
        Long portfolioId = currentUser.getPortfolio().getId();

        model.addAttribute("fundTransactions",
                fundTransactionRepository.findByFundIdOrderByCreatedAtDesc(fundId));
        model.addAttribute("investmentTransactions",
                investmentTransactionRepository.findByInvestmentPortfolioIdOrderByCreatedAtDesc(portfolioId));
        return "transactions/list";
    }
}
