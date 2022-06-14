package com.example.portfoliosOverview.controllers;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.services.PortfolioService;
import com.example.portfoliosOverview.services.StockService;
import com.example.portfoliosOverview.webScraper.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolios")
@CrossOrigin(origins = "*")
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

    @GetMapping("/name/{portfolioName}")
    public List<Portfolio> getPortfoliosByName(@PathVariable String portfolioName) {
        return portfolioService.getPortfoliosByName(portfolioName);
    }

    @PostMapping("/{portfolioId}/stocks/add")
    public void addStock(@PathVariable Long portfolioId, @RequestParam String name, @RequestParam String displayName,
                          @RequestParam int amountOfShares, @RequestParam boolean isUS, @RequestParam String cid) {
        Integer parsedCid = cid.equals("") ? null : Integer.parseInt(cid);
        portfolioService.addStock(portfolioId, new Stock(name, displayName, amountOfShares, isUS, parsedCid));
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        Stock stock = portfolioService.getStocksInPortfolioByName(portfolioId, name).get(0);
        webScraper.updateStockInPortfolio(stock);
        webScraper.updatePortfolio(portfolio);
    }

    @PostMapping("/add")
    public void addPortfolio(@RequestParam String name) {
        portfolioService.addPortfolio(new Portfolio(name));
    }

    @DeleteMapping("{id}")
    public void deletePortfolio(@PathVariable Long id) throws Exception {
        portfolioService.deleteById(id);
    }
}


