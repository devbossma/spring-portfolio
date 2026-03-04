package dev.saberlabs.myspringportfolio.investment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/investments")
public class InvestmentController {

    private final InvestmentRepository investmentRepository;

    public InvestmentController(InvestmentRepository investmentRepository) {
        this.investmentRepository = investmentRepository;
    }

    @GetMapping("")
    public String listInvestments(Model model) {
        model.addAttribute("investments", investmentRepository.findAll());
        return "investments/list";
    }

    @PostMapping("")
    public String addInvestment(InvestmentEntity investment) {
        investmentRepository.save(investment);
        return "redirect:/investments";
    }
}
