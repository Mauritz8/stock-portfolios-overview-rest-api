package com.example.portfoliosOverview.models;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.List;

@Table(name = "portfolios")
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.None.class
)
public class Portfolio {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double totalMoneyInvested;

    private Double percentChange1Day;
    private Double percentChange1Week;
    private Double percentChange1Month;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "portfolio")
    @JsonIgnoreProperties("portfolio")
    private List<Stock> stocks;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

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

    public Double getTotalMoneyInvested() {
        return totalMoneyInvested;
    }

    public void setTotalMoneyInvested(Double totalMoneyInvested) {
        this.totalMoneyInvested = totalMoneyInvested;
    }

    public Double getPercentChange1Day() {
        return percentChange1Day;
    }

    public void setPercentChange1Day(Double percentChange1Day) {
        this.percentChange1Day = percentChange1Day;
    }

    public Double getPercentChange1Week() {
        return percentChange1Week;
    }

    public void setPercentChange1Week(Double percentChange1Week) {
        this.percentChange1Week = percentChange1Week;
    }

    public Double getPercentChange1Month() {
        return percentChange1Month;
    }

    public void setPercentChange1Month(Double percentChange1Month) {
        this.percentChange1Month = percentChange1Month;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
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