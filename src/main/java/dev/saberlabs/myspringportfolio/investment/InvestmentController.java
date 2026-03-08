package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

//    @GetMapping("")
//    public String listInvestments(Model model, @AuthenticationPrincipal UserEntity currentUser) {
//        model.addAttribute("investments", investmentService.listInvestmentsByPortfolioId(currentUser.getPortfolio().getId()));
//        return "investments/list";
//    }
//
//    @PostMapping("")
//    public String addInvestment(InvestmentEntity investment) {
//        investmentService.addInvestment(investment);
//        return "redirect:/investments";
//    }
}
