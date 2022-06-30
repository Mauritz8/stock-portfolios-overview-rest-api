package com.example.portfoliosOverview.services;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.repositories.StockRepository;
import com.example.portfoliosOverview.webScraper.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    StockRepository stockRepository;

    @Autowired
    WebScraper webScraper;

    public List<Stock> getStocks() {
        return stockRepository.findAll();
    }

    public Stock getStockById(Long id) {
        return stockRepository.findById(id).orElse(null);
    }

    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public void deleteById(long id) {
        stockRepository.deleteById(id);
    }

    public void updateStock(Stock stock, int newAmountOfShares) throws Exception {
        if (newAmountOfShares <= 0) {
            throw new Exception("You need 1 share at the minimum");
        }
        stock.setAmountOfShares(newAmountOfShares);
        double moneyInvestedInStock = webScraper.getMoneyInvestedInStock(stock, stock.getCurrentPrice());
        stock.setMoneyInvestedInStock(moneyInvestedInStock);
        stockRepository.save(stock);

        Portfolio portfolio = stock.getPortfolio();
        webScraper.updatePortfolio(portfolio);
    }
}
