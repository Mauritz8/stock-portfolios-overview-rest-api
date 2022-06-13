package com.example.portfoliosOverview.exceptions.stockNotExist;

public class StockNotExistException extends RuntimeException {

    public StockNotExistException(String stockName) {
        super("There is no stock with the name " + stockName);
    }
}
