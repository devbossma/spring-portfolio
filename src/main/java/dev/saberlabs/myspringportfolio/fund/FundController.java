package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/funds")
/*
 * Handles HTTP POST requests for fund operations: depositing capital and withdrawing capital.
 * Both operations redirect back to /portfolio on completion, passing any error messages via flash attributes.
 * */
public class FundController {

    private final FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @PostMapping("/add")
    /*
     * Adds a specified amount of capital to the authenticated user's fund.
     * Params:
     * - currentUser: The authenticated user whose fund will be credited.
     * - amount: The amount of capital to deposit.
     * - redirectAttributes: Used to pass error messages back to the portfolio view.
     * Returns: A redirect to /portfolio.
     * */
    public String addToFund(@AuthenticationPrincipal UserEntity currentUser,
                            @RequestParam BigDecimal amount,
                            RedirectAttributes redirectAttributes) {
        try {
            fundService.addToFund(currentUser.getPortfolio().getFund().getId(), amount, currentUser);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("fundError", e.getMessage());
        }
        return "redirect:/portfolio";
    }

    @PostMapping("/withdraw")
    /*
     * Withdraws a specified amount of capital from the authenticated user's fund.
     * Will fail if the requested amount exceeds available dry powder (uninvested capital).
     * Params:
     * - currentUser: The authenticated user whose fund will be debited.
     * - amount: The amount of capital to withdraw.
     * - redirectAttributes: Used to pass error messages back to the portfolio view.
     * Returns: A redirect to /portfolio.
     * */
    public String withdraw(@AuthenticationPrincipal UserEntity currentUser,
                           @RequestParam BigDecimal amount,
                           RedirectAttributes redirectAttributes) {
        try {
            fundService.withdraw(currentUser.getPortfolio().getFund().getId(), amount, currentUser);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("fundError", e.getMessage());
        }
        return "redirect:/portfolio";
    }
}
