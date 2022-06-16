package com.example.portfoliosOverview.controllers;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.services.PortfolioService;
import com.example.portfoliosOverview.services.StockService;
import com.example.portfoliosOverview.webScraper.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    @Autowired
    StockService stockService;

    @Autowired
    PortfolioService portfolioService;

    @Autowired
    WebScraper webScraper;

    @GetMapping("best")
    public Stock getBestStock() {
        return stockService.getBestStock();
    }

    @GetMapping("worst")
    public Stock getWorstStock() {
        return stockService.getWorstStock();
    }

    @DeleteMapping("{id}")
    public void deleteStock(@PathVariable Long id) throws Exception {
        Stock stock = stockService.getStockById(id);
        Long portfolioId = stock.getPortfolio().getId();
        stockService.deleteById(id);
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        webScraper.updatePortfolio(portfolio);
    }
}
