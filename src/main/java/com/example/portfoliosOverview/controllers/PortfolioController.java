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
        //portfolioService.addPortfolio(new Portfolio("Martin portfolio"));
        //portfolioService.addStock(4L, new Stock("tesla-motors", "Tesla", 1, null, true, null));
        //portfolioService.addStock(4L, new Stock("facebook-inc", "Facebook", 2, null, true, null));

        //portfolioService.addPortfolio(new Portfolio("Mauritz portfolio"));
        //portfolioService.addStock(1L, new Stock("axfood-ab", "Axfood", 50, null, false, 25978));
        //portfolioService.addStock(1L, new Stock("visa-inc", "Visa", 2, null, true, null));
        //portfolioService.addStock(1L, new Stock("mastercard-cl-a", "Mastercard", 1, null, true, null));
        //portfolioService.addStock(1L, new Stock("microsoft-corp", "Microsoft", 1, null, true, null));
        //portfolioService.addStock(1L, new Stock("securitas-b", "Securitas", 3, null, false, null));

        //webScraper.main();
        return portfolioService.getPortfolios();
    }
}


