package com.example.portfoliosOverview.controllers;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.services.PortfolioService;
import com.example.portfoliosOverview.webScraper.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portfolios")
@CrossOrigin(origins = "http://localhost:3000")
public class PortfolioController {

    @Autowired
    PortfolioService portfolioService;

    @Autowired
    WebScraper webScraper;

    @GetMapping("")
    public List<Portfolio> getPortfolios() {
        //portfolioService.addStock(29L, new Stock("tesla-motors", "Tesla", 1, null, true, null));
        //portfolioService.addStock(29L, new Stock("facebook-inc", "Facebook", 2, null, true, null));
        //portfolioService.addPortfolio(new Portfolio("Martin portfolio"));
        //webScraper.main();
        return portfolioService.getPortfolios();
    }
}


