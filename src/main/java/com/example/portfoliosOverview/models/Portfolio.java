package com.example.portfoliosOverview.models;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.List;

@Table(name = "portfolios")
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.None.class
)
public class Portfolio {

    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double percentChange;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "portfolio")
    @JsonIgnoreProperties("portfolio")
    private List<Stock> stocks;

    public Portfolio() {
    }

    public Portfolio(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(Double percentChange) {
        this.percentChange = percentChange;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
        stock.setPortfolio(this);
    }

    public void removeStock(Stock stock) {
        stocks.remove(stock);
        stock.setPortfolio(null);
    }
}