package com.example.portfoliosOverview.services;

import com.example.portfoliosOverview.exceptions.portfolioWithNameAlreadyExists.PortfolioWithNameAlreadyExistsException;
import com.example.portfoliosOverview.exceptions.stockAlreadyInPortfolio.StockAlreadyInPortfolioException;
import com.example.portfoliosOverview.exceptions.stockNotExist.StockNotExistException;
import com.example.portfoliosOverview.exceptions.stockWithCidNotExist.StockWithCidNotExistException;
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

    public Portfolio addPortfolio(Portfolio portfolio) {
        List<Portfolio> portfolios = portfolioRepository.findByName(portfolio.getName());
        if (portfolios.size() > 0) {
            throw new PortfolioWithNameAlreadyExistsException(portfolio.getName());
        }
        return portfolioRepository.save(portfolio);
    }

    public void addStock(Long portfolioId, Stock stock) {
        Portfolio portfolio = portfolioRepository.getById(portfolioId);

        // check if stock is already in portfolio
        List<Stock> stocksWithNameInPortfolio = portfolioRepository.findStocksInPortfolioByName(portfolio.getId(), stock.getName());
        if (stocksWithNameInPortfolio.size() > 0) {
            throw new StockAlreadyInPortfolioException(stock.getName(), portfolio.getName());
        }

        if (!webScraper.stockExists(stock)) {
            if (stock.getCid() == null) {
                throw new StockNotExistException(stock.getName());
            }
            throw new StockWithCidNotExistException(stock.getName(), stock.getCid());
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
}
