package dev.saberlabs.myspringportfolio.investment;

import dev.saberlabs.myspringportfolio.user.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/investments")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping("")
    public String listInvestments(@AuthenticationPrincipal UserEntity currentUser, Model model,
                                  @RequestParam(required = false) String sort) {
        var investments = investmentService.listInvestmentsByPortfolioId(currentUser.getPortfolio().getId());
        if ("amount_asc".equals(sort)) {
            investments.sort((a, b) -> a.getInvestedAmount().compareTo(b.getInvestedAmount()));
        } else if ("amount_desc".equals(sort)) {
            investments.sort((a, b) -> b.getInvestedAmount().compareTo(a.getInvestedAmount()));
        } else if ("name".equals(sort)) {
            investments.sort((a, b) -> a.getName().compareTo(b.getName()));
        }
        model.addAttribute("investments", investments);
        model.addAttribute("sort", sort);
        return "investments/list";
    }

    @PostMapping("")
    public String addInvestment(@AuthenticationPrincipal UserEntity currentUser, InvestmentEntity investment) {
        investment.setPortfolio(currentUser.getPortfolio());
        investmentService.addInvestment(investment);
        return "redirect:/investments";
    }

    @PostMapping("/update")
    public String updateInvestmentName(@RequestParam Long id, @RequestParam String name) {
        investmentService.updateInvestmentName(id, name);
        return "redirect:/investments";
    }

    @PostMapping("/update-value")
    public String updateInvestmentCurrentValue(@RequestParam Long id, @RequestParam BigDecimal currentValue) {
        investmentService.updateInvestmentCurrentValue(id, currentValue);
        return "redirect:/investments";
    }

    @PostMapping("/exit")
    public String exitInvestment(@RequestParam Long id, @RequestParam BigDecimal exitValue) {
        investmentService.exitInvestment(id, exitValue);
        return "redirect:/investments";
    }

    @GetMapping("/{id}")
    public String viewInvestment(@PathVariable Long id, Model model) {
        InvestmentEntity investment = investmentService.getInvestmentById(id);
        model.addAttribute("investment", investment);
        return "investments/view";
    }

    @GetMapping("/{id}/edit")
    public String editInvestment(@PathVariable Long id, Model model) {
        InvestmentEntity investment = investmentService.getInvestmentById(id);
        model.addAttribute("investment", investment);
        return "investments/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateInvestment(@PathVariable Long id,
                                   @RequestParam String name,
                                   @RequestParam InvestmentSector sector,
                                   @RequestParam InvestmentStatus status,
                                   @RequestParam InvestmentStage stage,
                                   @RequestParam RiskLevel riskLevel,
                                   @RequestParam BigDecimal pricePerUnit,
                                   @RequestParam Integer quantity,
                                   @RequestParam BigDecimal currentValue) {
        InvestmentEntity existing = investmentService.getInvestmentById(id);
        existing.setName(name);
        existing.setSector(sector);
        existing.setStatus(status);
        existing.setStage(stage);
        existing.setRiskLevel(riskLevel);
        existing.setPricePerUnit(pricePerUnit);
        existing.setQuantity(quantity);
        existing.setCurrentValue(currentValue);
        // Recalculate invested amount
        existing.setInvestedAmount(pricePerUnit.multiply(java.math.BigDecimal.valueOf(quantity)));

        investmentService.updateInvestment(existing);
        return "redirect:/investments/" + id;
    }
}
