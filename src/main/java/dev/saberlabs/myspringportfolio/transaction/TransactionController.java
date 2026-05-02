package dev.saberlabs.myspringportfolio.transaction;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transactions")
/*
 * Handles the transaction ledger view, displaying all fund and investment transactions for the authenticated user.
 * Fetches fund transactions for the user's fund and investment transactions for the user's portfolio,
 * both ordered chronologically (most recent first).
 * */
public class TransactionController {

    private final FundTransactionRepository fundTransactionRepository;
    private final InvestmentTransactionRepository investmentTransactionRepository;

    public TransactionController(FundTransactionRepository fundTransactionRepository,
                                 InvestmentTransactionRepository investmentTransactionRepository) {
        this.fundTransactionRepository = fundTransactionRepository;
        this.investmentTransactionRepository = investmentTransactionRepository;
    }

    @GetMapping("")
    /*
     * Serves the full transaction ledger view.
     * Adds all fund transactions and all investment transactions to the model, ordered by date descending.
     * Params:
     * - currentUser: The authenticated user whose transaction history is displayed.
     * - model: The MVC model used to pass transaction lists to the view.
     * Returns: The "transactions/list" view.
     * */
    public String list(@AuthenticationPrincipal UserEntity currentUser, Model model) {
        Long fundId      = currentUser.getPortfolio().getFund().getId();
        Long portfolioId = currentUser.getPortfolio().getId();

        model.addAttribute("fundTransactions",
                fundTransactionRepository.findByFundIdOrderByCreatedAtDesc(fundId));
        model.addAttribute("investmentTransactions",
                investmentTransactionRepository.findByPortfolioIdOrderByCreatedAtDesc(portfolioId));
        return "transactions/list";
    }
}
