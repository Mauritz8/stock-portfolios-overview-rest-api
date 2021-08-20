package com.example.portfoliosOverview.services;

import com.example.portfoliosOverview.models.Portfolio;
import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.repositories.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    @Autowired
    PortfolioRepository portfolioRepository;

    public List<Portfolio> getPortfolios() {
        return portfolioRepository.findAll();
    }

    public Portfolio addPortfolio(Portfolio portfolio) {
        return portfolioRepository.save(portfolio);
    }

    public Stock addStock(Long portfolioId, Stock stock) {
        Portfolio portfolio = portfolioRepository.getById(portfolioId);
        portfolio.addStock(stock);
        portfolioRepository.save(portfolio);
        return stock;
    }
}
