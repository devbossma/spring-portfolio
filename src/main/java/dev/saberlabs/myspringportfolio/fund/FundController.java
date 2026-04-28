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
public class FundController {

    private final FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @PostMapping("/add")
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
