package dev.saberlabs.myspringportfolio.fund;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/funds")
public class FundController {

    private final FundService fundService;

    public FundController(FundRepository fundRepository, FundService fundService) {
        this.fundService = fundService;
    }

    public String listFunds() {
        return "funds/list";
    }

    public boolean addFund(Long fund) {

        return true;
    }

    @PostMapping("/add")
    public String addToFund(@AuthenticationPrincipal UserEntity currentUser, @RequestParam BigDecimal amount) {
        fundService.addToFund(currentUser.getPortfolio().getFund().getId(), amount);
        return "redirect:/";
    }
}
