package com.example.portfoliosOverview.services;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.repositories.PortfolioRepository;
import com.example.portfoliosOverview.webScraper.WebScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    @Autowired
    PortfolioRepository portfolioRepository;

    @Autowired
    WebScraper webScraper;

    public List<Portfolio> getPortfolios() {
        return portfolioRepository.findAll();
    }

    public Portfolio addPortfolio(Portfolio portfolio) throws Exception {
        String name = portfolio.getName();
        List<Portfolio> portfolios = portfolioRepository.findByName(name);
        if (portfolios.size() > 0) {
            throw new Exception("There is already a portfolio with the name " + name);
        }
        return portfolioRepository.save(portfolio);
    }

    public void addStock(Long portfolioId, Stock stock) throws Exception {
        Portfolio portfolio = portfolioRepository.getById(portfolioId);

        // check if stock is already in portfolio
        List<Stock> stocksWithNameInPortfolio = portfolioRepository.findStocksInPortfolioByName(portfolio.getId(), stock.getName());
        if (stocksWithNameInPortfolio.size() > 0) {
            throw new Exception(stock.getName() + " in already in the portfolio " + portfolio.getName());
        }

        if (!webScraper.stockExists(stock)) {
            if (stock.getCid() == null) {
                throw new Exception("There is no stock with the name " + stock.getName());
            }
            throw new Exception("There is no stock with the name " + stock.getName() + " and cid " + stock.getCid());
        }

        portfolio.addStock(stock);
        portfolioRepository.save(portfolio);
    }

    public List<Portfolio> getPortfoliosByName(String portfolioName) {
        return portfolioRepository.findByName(portfolioName);
    }

    public Portfolio getPortfolioById(Long id) {
        return portfolioRepository.findById(id).orElse(null);
    }

    public List<Stock> getStocksInPortfolioByName(long portfolioId, String name) {
        return portfolioRepository.findStocksInPortfolioByName(portfolioId, name);
    }

    public void deleteById(long id) throws Exception {
        Portfolio portfolio = portfolioRepository.findById(id).orElse(null);
        if (portfolio == null) {
            throw new Exception("There is no portfolio with the id " + id);
        }
        portfolioRepository.deleteById(id);
    }
  
}
