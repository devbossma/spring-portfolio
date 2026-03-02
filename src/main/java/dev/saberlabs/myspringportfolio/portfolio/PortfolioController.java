package dev.saberlabs.myspringportfolio.portfolio;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortfolioController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

        @GetMapping("/portfolio")
    public String portfolio() { return "home";  }



}
