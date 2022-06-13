package com.example.portfoliosOverview.exceptions.stockAlreadyInPortfolio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class StockAlreadyInPortfolioAdvice {

    @ResponseBody
    @ExceptionHandler(StockAlreadyInPortfolioException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String stockAlreadyInPortfolioHandler(StockAlreadyInPortfolioException ex) {
        return ex.getMessage();
    }
}
