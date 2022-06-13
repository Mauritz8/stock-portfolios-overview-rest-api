package com.example.portfoliosOverview.exceptions.stockWithCidNotExist;

public class StockWithCidNotExistException extends RuntimeException {

    public StockWithCidNotExistException(String stockName, Integer cid) {
        super("There is no stock with the name " + stockName + " and cid " + cid);
    }
}
