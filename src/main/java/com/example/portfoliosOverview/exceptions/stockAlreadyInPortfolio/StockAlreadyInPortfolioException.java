package com.example.portfoliosOverview.exceptions.stockAlreadyInPortfolio;

public class StockAlreadyInPortfolioException extends RuntimeException {

    public StockAlreadyInPortfolioException(String stockName, String portfolioName) {
        super(stockName + " in already in the portfolio " + portfolioName);
    }
}
