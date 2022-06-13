package com.example.portfoliosOverview.controllers;

import com.example.portfoliosOverview.models.Stock;
import com.example.portfoliosOverview.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stocks")
@CrossOrigin(origins = "http://localhost:3000")
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
}
