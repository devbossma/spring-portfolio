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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Comparator;

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
            investments.sort(Comparator.comparing(InvestmentEntity::getName));
        }
        model.addAttribute("investments", investments);
        model.addAttribute("sort", sort);
        return "investments/list";
    }

    @PostMapping("")
    public String addInvestment(@AuthenticationPrincipal UserEntity currentUser,
                                InvestmentEntity investment,
                                RedirectAttributes redirectAttributes) {
        investment.setPortfolio(currentUser.getPortfolio());
        try {
            investmentService.addInvestment(investment, currentUser);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("investmentError", e.getMessage());
        }
        return "redirect:/investments";
    }

    @PostMapping("/update-value")
    public String updateInvestmentCurrentValue(@RequestParam Long id, @RequestParam BigDecimal currentValue) {
        InvestmentEntity investment = investmentService.getInvestmentById(id);
        if (investment.isActive()) {
            investmentService.updateInvestmentCurrentValue(id, currentValue);
        }
        return "redirect:/investments/" + id;
    }

    @PostMapping("/exit")
    public String exitInvestment(@AuthenticationPrincipal UserEntity currentUser,
                                 @RequestParam Long id,
                                 @RequestParam BigDecimal exitValue) {
        investmentService.exitInvestment(id, exitValue, currentUser);
        return "redirect:/investments/" + id;
    }

    @PostMapping("/write-off")
    public String writeOffInvestment(@AuthenticationPrincipal UserEntity currentUser,
                                     @RequestParam Long id) {
        InvestmentEntity investment = investmentService.getInvestmentById(id);
        if (investment.isActive()) {
            investmentService.writeOffInvestment(id, currentUser);
        }
        return "redirect:/investments/" + id;
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
        if (!investment.isActive()) {
            return "redirect:/investments/" + id;
        }
        model.addAttribute("investment", investment);
        return "investments/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateInvestment(@PathVariable Long id,
                                   @RequestParam String name,
                                   @RequestParam InvestmentType type,
                                   @RequestParam InvestmentSector sector,
                                   @RequestParam(required = false) InvestmentStage stage,
                                   @RequestParam RiskLevel riskLevel,
                                   @RequestParam BigDecimal pricePerUnit,
                                   @RequestParam Integer quantity,
                                   @RequestParam BigDecimal currentValue) {
        InvestmentEntity existing = investmentService.getInvestmentById(id);
        if (!existing.isActive()) {
            return "redirect:/investments/" + id;
        }
        existing.setName(name);
        existing.setType(type);
        existing.setSector(sector);
        existing.setStage(type == InvestmentType.PRIVATE ? stage : null);
        existing.setRiskLevel(riskLevel);
        existing.setPricePerUnit(pricePerUnit);
        existing.setQuantity(quantity);
        existing.setCurrentValue(currentValue);
        existing.setInvestedAmount(pricePerUnit.multiply(BigDecimal.valueOf(quantity)));

        investmentService.updateInvestment(existing);
        return "redirect:/investments/" + id;
    }
}
