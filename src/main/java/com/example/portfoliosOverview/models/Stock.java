package com.example.portfoliosOverview.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.springframework.lang.NonNull;

import javax.persistence.*;

@Table(name = "stocks")
@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Stock {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    // url name on investing.com
    private String name;

    private String displayName = name;

    private Double currentPrice;

    private Double moneyInvestedInStock;

    @Column(nullable = false)
    private Integer amountOfShares;

    private Double percentOfPortfolio;

    private Double percentChange1Day;
    private Double percentChange1Week;
    private Double percentChange1Month;

    @Column(nullable = false)
    private Boolean isUS;

    // on investing.com there is a url parameter called cid in the url of the stock (sometimes empty)
    private Integer cid;

    @ManyToOne(fetch = FetchType.EAGER)
    @NonNull
    @JsonIgnoreProperties("stocks")
    private Portfolio portfolio;


    public Stock() {
    }

    public Stock(String name, String displayName, Integer amountOfShares, Boolean isUS, Integer cid) {
        this.name = name;
        this.displayName = displayName;
        this.amountOfShares = amountOfShares;
        this.isUS = isUS;
        this.cid = cid;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getMoneyInvestedInStock() {
        return moneyInvestedInStock;
    }

    public void setMoneyInvestedInStock(Double moneyInvestedInStock) {
        this.moneyInvestedInStock = moneyInvestedInStock;
    }

    public Integer getAmountOfShares() {
        return amountOfShares;
    }

    public void setAmountOfShares(Integer amountOfShares) {
        this.amountOfShares = amountOfShares;
    }

    public Double getPercentOfPortfolio() {
        return percentOfPortfolio;
    }

    public void setPercentOfPortfolio(Double percentOfPortfolio) {
        this.percentOfPortfolio = percentOfPortfolio;
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

    public Boolean isUS() {
        return isUS;
    }

    public void setUS(Boolean US) {
        isUS = US;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    @NonNull
    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(@NonNull Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}