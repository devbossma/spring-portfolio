package dev.saberlabs.myspringportfolio.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/investments")
public class InvestmentController {
    @GetMapping("")
    public void home() {
        // Logic to display the home page or dashboard
    }
}