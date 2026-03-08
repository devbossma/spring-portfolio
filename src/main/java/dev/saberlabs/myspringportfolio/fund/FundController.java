package dev.saberlabs.myspringportfolio.fund;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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


}
