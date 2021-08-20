package com.example.portfoliosOverview.services;

import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    StockRepository stockRepository;

    public List<Stock> getStocks() {
        return stockRepository.findAll();
    }

    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock getBestStock() {
        return stockRepository.findFirstByOrderByPercentChangeDesc();
    }

    public Stock getWorstStock() {
        return stockRepository.findFirstByOrderByPercentChangeAsc();
    }
}
