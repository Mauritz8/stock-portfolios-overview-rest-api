package com.example.portfoliosOverview.exceptions.portfolioWithNameAlreadyExists;

public class PortfolioWithNameAlreadyExistsException extends RuntimeException {

    public PortfolioWithNameAlreadyExistsException(String name) {
        super("There is already a portfolio with the name " + name);
    }
}
