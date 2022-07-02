package com.example.portfoliosOverview.controllers;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.services.PortfolioService;
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
        return portfolioService.getPortfolios();
    }

    @GetMapping("/notInCompetition")
    public List<Portfolio> getPortfoliosNotInCompetition() {
        return portfolioService.getPortfoliosNotInCompetition();
    }

    @GetMapping("/name/{portfolioName}")
    public List<Portfolio> getPortfoliosByName(@PathVariable String portfolioName) {
        return portfolioService.getPortfoliosByName(portfolioName);
    }

    @PostMapping("/{portfolioId}/stocks/add")
    public void addStock(@PathVariable Long portfolioId, @RequestParam String name,
                         @RequestParam String displayName, @RequestParam int amountOfShares,
                         @RequestParam boolean isUS, @RequestParam String cid) throws Exception {
        Integer parsedCid = cid.equals("") ? null : Integer.parseInt(cid);
        portfolioService.addStock(portfolioId, new Stock(name, displayName, amountOfShares, isUS, parsedCid));
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        Stock stock = portfolioService.getStocksInPortfolioByName(portfolioId, name).get(0);
        webScraper.updateStockInPortfolio(stock);
        webScraper.updatePortfolio(portfolio);
    }

    @PostMapping("/add")
    public void addPortfolio(@RequestParam String name) throws Exception {
        portfolioService.addPortfolio(new Portfolio(name));
    }

    @DeleteMapping("{id}")
    public void deletePortfolio(@PathVariable Long id) throws Exception {
        portfolioService.deleteById(id);
    }

    @PutMapping("{id}")
    public void updatePortfolio(@PathVariable Long id, @RequestParam String newName) throws Exception {
        portfolioService.updatePortfolio(id, newName);
    }
}


