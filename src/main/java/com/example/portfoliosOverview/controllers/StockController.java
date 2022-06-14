package com.example.portfoliosOverview.controllers;

import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "*")
public class StockController {

    @Autowired
    StockService stockService;

    @GetMapping("best")
    public Stock getBestStock() {
        return stockService.getBestStock();
    }

    @GetMapping("worst")
    public Stock getWorstStock() {
        return stockService.getWorstStock();
    }

    @DeleteMapping("{id}")
    public void deleteStock(@PathVariable Long id) throws Exception {
        stockService.deleteById(id);
    }
}
