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

    public Stock getStockById(Long id) {
        return stockRepository.findById(id).orElse(null);
    }

    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public void deleteById(long id) throws Exception {
        Stock stock = stockRepository.findById(id).orElse(null);
        if (stock == null) {
            throw new Exception("There is no stock with the id " + id);
        }
        stockRepository.deleteById(id);
    }
}
