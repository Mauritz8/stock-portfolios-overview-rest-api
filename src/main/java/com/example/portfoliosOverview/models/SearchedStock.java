package com.example.portfoliosOverview.models;

public class SearchedStock {

    private String name;
    private String exchange;
    private String cid;

    public SearchedStock(String name, String exchange, String cid) {
        this.name = name;
        this.exchange = exchange;
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
