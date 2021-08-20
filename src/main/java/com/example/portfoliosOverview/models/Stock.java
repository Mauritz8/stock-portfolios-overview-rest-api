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

    @GeneratedValue
    @Id
    private Long id;

    @Column(nullable = false)
    // url name on investing.com
    private String name;

    private String displayName = name;

    @Column(nullable = false)
    private Integer amountOfShares;

    private Double percentOfPortfolio;

    private Double percentChange;

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

    public Stock(String name, String displayName, Integer amountOfShares, Double percentChange, Boolean isUS, Integer cid) {
        this.name = name;
        this.displayName = displayName;
        this.amountOfShares = amountOfShares;
        this.percentChange = percentChange;
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

    public Double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(Double percentChange) {
        this.percentChange = percentChange;
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